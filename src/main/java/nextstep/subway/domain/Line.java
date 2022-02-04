package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @Embedded
    private Sections sections;

    public void addSection(
            Station upStation,
            Station downStation,
            int distance
    ) {
        sections.addSection(new Section(upStation, downStation, distance));
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    protected Line() {
    }

    public Line(
            String name,
            String color,
            Station upStation,
            Station downStation,
            int initialDistance
    ) {
        this.name = name;
        this.color = color;
        var sectionList = new LinkedList<Section>();
        sectionList.add(new Section(upStation, downStation, initialDistance));
        this.sections = new Sections(sectionList);
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

    public void removeSection(Station station) {
        sections.remove(station);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        var line = (Line) obj;
        return StringUtils.equals(this.name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
