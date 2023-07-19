package nextstep.subway.line;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    public Line() {}

    public Line(final String name, final String color, final Section section) {
        this(null, name, color, new Sections(List.of(section)));
        this.sections.updateLine(this);
    }

    public Line(final Long id,final String name,final String color,final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public void update(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public Section getLastSection() {
        return this.sections.getLastSection();
    }

    public void deleteSection(final Station station) {
        this.sections.delete(station);
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

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public List<Section> getSections() {
        return this.sections.getSections();
    }
}
