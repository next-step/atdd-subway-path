package nextstep.subway.line.domain;

import nextstep.subway.station.Station;

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

    public void deleteSection(Station station) {
        sections.removeSection(station);
    }

    public void generateSection(int distance, Station upStation, Station downStation) {
        Section section = new Section(distance, upStation, downStation, this);
        sections.addSection(section);
    }
}
