package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        Set<String> stations = new HashSet<>();
        return sections.stream()
                .map(section -> List.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .filter(station -> {
                    if (stations.contains(station.getName())) {
                        return false;
                    }
                    stations.add(station.getName());
                    return true;
                }).collect(Collectors.toUnmodifiableList());
    }

    public void removeSection(String stationName) {
        sections.stream()
                .filter(section -> section.getDownStation().getName().equals(stationName)
                        || section.getUpStation().getName().equals(stationName))
                .findFirst()
                .ifPresent(section -> sections.remove(section));
    }
}
