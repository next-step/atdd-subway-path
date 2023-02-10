package nextstep.subway.domain;

import nextstep.subway.common.ErrorMessage;

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

    public void addSections(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, sections.get(0).getUpStation());

        return stations;
    }

    public void removeStation(Station station) {
        int index = sections.size() - 1;

        if (index == 0) {
            throw new IllegalStateException(ErrorMessage.ENOUGH_NOT_SECTION_SIZE.toString());
        }
        if (!sections.get(index).getDownStation().equals(station)) {
            throw new IllegalArgumentException(ErrorMessage.ENOUGH_REMOVE_DOWN.toString());
        }

        sections.remove(index);
    }
}
