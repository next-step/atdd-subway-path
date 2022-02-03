package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

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

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(new Section(upStation, downStation, distance));
    }

    public Section addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(upStation, downStation, distance);
        section.updateLine(this);
        return sections.addSection(section);
    }

    public List<Station> getAllStations() {
        return sections.getAllStations();
    }

    public Section removeSection(Station station) {
        Section removedSection = sections.removeSection(station);
        removedSection.updateLine(null);
        return removedSection;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
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

    public Sections getSections() {
        return sections;
    }
}
