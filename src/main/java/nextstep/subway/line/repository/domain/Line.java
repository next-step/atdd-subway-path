package nextstep.subway.line.repository.domain;


import nextstep.subway.station.repository.domain.Station;

import javax.persistence.*;
import java.util.Collections;
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
    private Sections sections = new Sections();

    @Column(nullable = false)
    private int distance;

    protected Line() {
    }

    public Line(final String name, final String color, final Section section) {
        this.name = name;
        this.color = color;
        addSection(section);
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
        return Collections.unmodifiableList(sections.getStations());
    }

    public int getDistance() {
        return distance;
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public void addSection(final Section section) {
        sections.connect(section);
        distance += section.getDistance();
    }
    public void removeSectionByStation(final Station station) {
        final int lastSectionDistance = sections.getLastSectionDistance();
        distance -= lastSectionDistance;
        sections.disconnectLastSection(station);
    }
}
