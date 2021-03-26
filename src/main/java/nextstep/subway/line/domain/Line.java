package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.application.InvalidSectionFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(Long userId, String name, String color) {
        if (name.length() < 2 || name.length() > 10) {
            throw new InvalidLineException();
        }
        this.userId = userId;
        this.name = name;
        this.color = color;
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    public void addSection(Station upStation, Station downStation, int distance, int duration) {
        boolean isUpStationExisted = getStations().stream().anyMatch(it -> it == upStation);
        boolean isDownStationExisted = getStations().stream().anyMatch(it -> it == downStation);

        if (isUpStationExisted && isDownStationExisted) {
            throw new InvalidSectionFoundException("이미 등록된 구간 입니다.");
        }

        if (!getSections().isEmpty() && getStations().stream().noneMatch(it -> it == upStation) &&
                getStations().stream().noneMatch(it -> it == downStation)) {
            throw new InvalidSectionFoundException("등록할 수 없는 구간 입니다.");
        }

        if (getSections().isEmpty()) {
            sections.add(new Section(this, upStation, downStation, distance, duration));
            return;
        }

        if (isUpStationExisted) {
            sections.stream()
                    .filter(it -> it.getUpStation() == upStation)
                    .findFirst()
                    .ifPresent(it -> it.updateUpStation(downStation, distance, duration));

            sections.add(new Section(this, upStation, downStation, distance, duration));
        }

        if (isDownStationExisted) {
            sections.stream()
                    .filter(it -> it.getDownStation() == downStation)
                    .findFirst()
                    .ifPresent(it -> it.updateDownStation(upStation, distance, duration));

            sections.add(new Section(this, upStation, downStation, distance, duration));
        }
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new InvalidSectionFoundException("제거할 구간이 없습니다.");
        }

        Optional<Section> upLineStation = sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();
        Optional<Section> downLineStation = sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst();

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            Station newUpStation = downLineStation.get().getUpStation();
            Station newDownStation = upLineStation.get().getDownStation();
            int newDistance = upLineStation.get().getDistance() + downLineStation.get().getDistance();
            int newDuration = upLineStation.get().getDuration() + downLineStation.get().getDuration();
            sections.add(new Section(this, newUpStation, newDownStation, newDistance, newDuration));
        }

        upLineStation.ifPresent(it -> sections.remove(it));
        downLineStation.ifPresent(it -> sections.remove(it));
    }
}
