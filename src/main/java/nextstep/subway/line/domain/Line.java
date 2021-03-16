package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(this, upStation, downStation, distance);

    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if (getSections().isEmpty()) {
            return stations;
        }

        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getSections().stream()
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
        Station downStation = getSections().get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getSections().stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }
        return downStation;
    }

    public void removeSection(Long stationId) {
        checkSectionRemoveValidity(stationId);
        List<Section> sectionsToRemove = getSectionToRemove(stationId);
        if (sectionsToRemove.size() > 1) {
            getSections().removeAll(sectionsToRemove);
            int newDistance = sectionsToRemove.stream().mapToInt(it -> it.getDistance()).sum();
            getSections().add(new Section(this, sectionsToRemove.get(0).getUpStation(), sectionsToRemove.get(1).getDownStation(), newDistance));
            return;
        }
        getSections().stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> getSections().remove(it));
    }

    private List<Section> getSectionToRemove(Long stationId) {
        return getSections().stream()
                .filter(it -> it.getUpStation().getId() == stationId || it.getDownStation().getId() == stationId)
                .collect(Collectors.toList());
    }

    private void checkSectionRemoveValidity(Long stationId) {
        if (getSections().size() <= 1) {
            throw new RuntimeException("마지막 남은 구간은 삭제할 수 없습니다.");
        }
        if (getSections().stream().noneMatch(it -> it.getDownStation().getId() == stationId || it.getUpStation().getId() == stationId)) {
            throw new RuntimeException("등록되지 않은 역은 삭제할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
