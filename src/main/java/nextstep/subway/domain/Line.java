package nextstep.subway.domain;

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
    private final Sections sections = new Sections();

    protected Line() {
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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.get();
    }

    public void addSection(Section newSection) {
        sections.add(this, newSection);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Integer> getSectionDistances() {
        return sections.getSectionDistances();
    }

    public void removeSection(Station station) {
        this.sections.removeSection(station);
    }

    public void removeSection(String stationName) {
        this.sections.removeSection(stationName);
    }

    public void updateLine(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
