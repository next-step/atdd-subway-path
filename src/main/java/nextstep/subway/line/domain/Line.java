package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

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
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (getStations().size() == 0) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }
        checkSectionAddValidty(upStation, downStation);
        sections.add(new Section(this, upStation, downStation, distance));
    }

    private void checkSectionAddValidty(Station upStation, Station downStation) {
        if (getStations().get(getStations().size() - 1) != upStation) {
            throw new RuntimeException("상행역은 하행 종점역이어야 합니다.");
        }

        if (getStations().stream().anyMatch(it -> it == downStation)) {
            throw new RuntimeException("하행역이 이미 등록되어 있습니다.");
        }
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
        getSections().stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> getSections().remove(it));
    }

    private void checkSectionRemoveValidity(Long stationId) {
        if (getSections().size() <= 1) {
            throw new RuntimeException();
        }
        if (getStations().get(getStations().size() - 1).getId() != stationId) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
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
