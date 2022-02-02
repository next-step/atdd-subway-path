package nextstep.subway.domain;

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

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station startingStation, Station endingStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, startingStation, endingStation, distance));
    }

    Line(Long id, String name, String color, Station startingStation, Station endingStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        sections.add(new Section(this, startingStation, endingStation, distance));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void update(String name, String color) {
        if (!Objects.isNull(name)) {
            this.name = name;
        }
        if (!Objects.isNull(color)) {
            this.color = color;
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Station startingStation, Station endingStation, int distance) {
        sections.add(new Section(this, startingStation, endingStation, distance));
    }

    public List<Station> getStations() {
        Map<Station, Station> stationMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Set<Station> downStationSet = new HashSet<>(stationMap.values());
        Station startingStation = stationMap.keySet().stream()
                .filter(Predicate.not(downStationSet::contains))
                .findAny().orElseThrow(IllegalStateException::new);

        List<Station> stations = new ArrayList<>();
        stations.add(startingStation);
        while (stationMap.containsKey(startingStation)) {
            startingStation = stationMap.get(startingStation);
            stations.add(startingStation);
        }
        return stations;
    }

    public void removeSection(Station station) {
        Map<Station, Station> stationMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
        Set<Station> upStationSet = stationMap.keySet();
        Station endingStation = stationMap.values().stream()
                .filter(Predicate.not(upStationSet::contains))
                .findAny().orElseThrow(IllegalStateException::new);
        if (!endingStation.equals(station)) {
            throw new IllegalArgumentException("구간이 목록에서 마지막 역이 아닙니다.");
        }

        Section section = sections.stream()
                .filter(_section -> _section.getDownStation().equals(station))
                .findAny().orElseThrow(() -> new IllegalArgumentException("구간에 존재하지 않는 역입니다."));
        sections.remove(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
