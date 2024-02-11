package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/** 지하철 노선 엔티티 */
@Table(name = "line")
@Entity
public class Line {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 지하철 노선명 */
    @Column(length = 20, nullable = false)
    private String name;

    /** 지하철 노선 색 */
    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {}

    private Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    private Line(final Long id, final String name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static Line of(final String name, final String color) {
        return new Line(null, name, color);
    }

    public Line update(final String name, final String color) {
        return new Line(id, name, color, sections);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(
            Section.of(
                this,
                upStation,
                downStation,
                distance
            )
        );
    }

    public void removeSectionByStation(Station station) {
        this.sections.remove(station);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
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

    public List<Section> getAllSections() {
        return sections.getSections();
    }
}
