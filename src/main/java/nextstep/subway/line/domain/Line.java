package nextstep.subway.line.domain;

import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.List;

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

    public List<Section> getSectionList() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void deleteSection(Station station) {
        sections.removeSection(station, this);
    }

    public void generateSection(int distance, Station upStation, Station downStation) {
        Section section = new Section(distance, upStation, downStation, this);
        sections.addSection(section);
    }
}
