package nextstep.subway.domain;

import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
        this.sections.add(section);
    }

    public List<Station> getStations() {
        if (CollectionUtils.isEmpty(sections)) {
            return Collections.emptyList();
        }
        LinkedList<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toCollection(LinkedList::new));
        stations.addFirst(sections.get(0).getUpStation());
        return stations;
    }

    public void removeSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }
}
