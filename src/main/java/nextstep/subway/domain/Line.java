package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(final String name, final String color) {
        this(null, name, color, new Sections(new ArrayList<>()));
    }

    public Line(final Long id, final String name, final String color, final Station upStation, final Station downStation, final Integer distance) {
        this(id, name, color, new Sections(List.of(new Section(upStation, downStation, distance))));
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

        validateSections();
        final int index = this.sections.getSections().size() - 1;
        validateMatchStation(downStation, index);

        this.sections.remove(index);
    }

    public List<Station> convertToStation() {
        return this.sections.getSections().stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateMatchStation(final Station downStation, final int index) {
        final Section section = this.sections.findByIndex(index);
        if (!section.getDownStation().equals(downStation)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateSections() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException();
        }
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

    public List<Section> getSectionsList() {
        return this.sections.getSections();
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
