package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
        getSections().add(section);
    }

    public List<Station> getStations() {
        List<Section> sections = getSections();
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(sections.get(sections.size() - 1).getDownStation());

        return stations;
    }

    public boolean isLastStation(Station station) {
        return getSections().get(getSections().size() - 1).getDownStation().equals(station);
    }

    public void removeSection() {
        getSections().remove(getSections().size() - 1);
    }
}
