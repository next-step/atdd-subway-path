package nextstep.subway.line.domain;

import java.util.List;
import javax.persistence.*;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        sections = new Sections(section.getUpStationId(), section.getDownStationId());

        sections.registerSection(section, this);
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

    public List<Section> getSections() {
        return sections.getSections();
    }

    public Long getFirstStationId() {
        return sections.getFirstStationId();
    }

    public Long getLastStationId() {
        return sections.getLastStationId();
    }

    public void removeSection(Station station) {
        sections.removeSection(station, this);
    }

    public void registerSection(Section newSection) {
        sections.registerSection(newSection, this);
    }
}
