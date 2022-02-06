package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    public List<Station> getAllStations() {
        return this.sections
                .stream()
                .flatMap(section -> section.getAllStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void deleteSection(Station station) {
        final int lastIndex = this.sections.size() - 1;
        final Section lastSection = this.sections.get(lastIndex);
        if (!lastSection.isDownStation(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(lastIndex);
    }
}
