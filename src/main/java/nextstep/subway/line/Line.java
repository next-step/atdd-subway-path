package nextstep.subway.line;

import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(final Long id, final String name, final String color, final List<Section> lineStations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections(lineStations);
    }

    public Line(final String name, final String color, final List<Section> lineStations) {
        this(null, name, color, lineStations);
    }

    public Line(final String name, final String color) {
        this(name, color, new ArrayList<>());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return this.color;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Section> addSection(Station upStation, Station downStation, Long distance) {
        return sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getStations() {
        return sections.getStations();
    }


    public long countOfStations() {
        return sections.countOfStations();
    }

    public List<Section> getSections() {
        return this.sections.getSections();
    }

    public Section getLastSection() {
        return this.sections.getSections()
            .stream()
            .filter(e -> e.getDownStation() == null)
            .findFirst()
            .orElseThrow();
    }
}
