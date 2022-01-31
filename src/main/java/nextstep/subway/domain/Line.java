package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station lastUpStation, Station lastDownStation, Distance distance) {
        this(name, color);
        sections.add(new Section(this, lastUpStation, lastDownStation, distance));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public boolean isSectionsEmpty() {
        return sections.isEmpty();
    }

    public int getSectionSize() {
        return sections.getSize();
    }

    public void removeSectionByLastDownStation(Station station) {
        sections.remove(station);
    }

    public Section getSectionAt(int i) {
        return sections.get(i);
    }

    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
