package nextstep.subway.domain;

import java.util.Arrays;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
    }

    public Line(Long lineId, String name, String color, Section section) {
        this.id = lineId;
        this.name = name;
        this.color = color;
        this.sections.add(section);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(String name, String color, Section section) {
        return new Line(name, color, section);
    }

    public static Line of(Long lineId, String name, String color, Section section) {
        return new Line(lineId, name, color, section);
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
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        if (sections.size() == 1) {
            return Arrays.asList(
                sections.get(0).getUpStation(),
                sections.get(0).getDownStation()
            );
        }

        return Arrays.asList(
            sections.get(0).getUpStation(),
            sections.get(0).getDownStation(),
            sections.get(1).getDownStation()
        );
    }

    private List<Station> createStationSortedFromSections() {
        return Arrays.asList();
    }
}
