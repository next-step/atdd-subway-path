package nextstep.subway.line.entity;

import nextstep.subway.station.entity.Station;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Line(final String name, final String color, final Station upStation, final Station downStation, final int distance) {
        Assert.hasText(name, "Line name cannot be empty.");
        Assert.hasText(color, "Line color cannot be empty.");
        Assert.notNull(upStation, "Line upStation cannot be null.");
        Assert.notNull(downStation, "Line downStation cannot be null.");
        Assert.isTrue(distance > 0, "Distance must be greater than 0.");

        this.name = name;
        this.color = color;

        List<Section> sectionList = new ArrayList<>();
        sectionList.add(new Section(this, upStation, downStation, distance));
        this.sections = Sections.from(sectionList);
    }

    public void updateDetails(final String name, final String color) {
        Assert.hasText(name, "Line name cannot be empty.");
        Assert.hasText(color, "Line color cannot be empty.");

        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        this.sections.addSection(section);
    }

    public void removeSection(final Station station) {
        this.sections.removeSection(station);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;

        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color +
                '}';
    }

}
