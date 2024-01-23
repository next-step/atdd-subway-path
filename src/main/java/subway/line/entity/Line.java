package subway.line.entity;

import subway.section.Section;
import subway.station.Station;

import javax.persistence.*;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void deleteSection(Long stationId) {
        sections.removeSection(stationId);
    }

    public void generateSection(int distance, Station upStation, Station downStation) {
        Section section = new Section(distance, upStation, downStation, this);
        sections.addSection(section);
    }
}
