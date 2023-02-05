package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    protected Line() {}

    private Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(final String name, final String color) {
        this(null, name, color, new Sections(new ArrayList<>()));
    }

    public Line(final String name, final String color, final Sections sections) {
        this(null, name, color, sections);
    }

    public Line(final String name, final String color, final Station upStation, final Station downStation, final Integer distance) {
        this(null, name, color, new Sections(List.of(new Section(upStation, downStation, distance))));
    }

    public void updateLine(final String changeName, final String changeColor) {
        this.name = changeName;
        this.color = changeColor;
    }

    public void addSection(final Station upStation, final Station downStation, final Integer distance) {
        this.sections.addSection(this, upStation, downStation, distance);
    }

    public void removeSection(final Station downStation) {
        this.sections.remove(downStation);
    }

    public List<Station> convertToStation() {
        return this.sections.convertToStations();
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
