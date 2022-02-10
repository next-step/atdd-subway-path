package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    @Transient
    private Section firstSection = new Section();
    @Transient
    private Section lastSection = new Section();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
        this.addSection(section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public void addSection(Station upStation, Station downStation, int distance) {

        if (this.sections.existUpStations(upStation)) {
            Section getSection = this.sections.getSectionFromUpStation(upStation);
            if (this.sections.isFirst(getSection)) {
                this.firstSection = getSection;
            }

        }

        if (this.sections.existDownStations(downStation)) {
            Section getSection = this.sections.getSectionFromDownStation(downStation);
            if (this.sections.isLast(getSection)) {
                this.lastSection = getSection;
            }
        }

        this.sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public Sections sections() {
        return this.sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName()) && Objects.equals(getColor(), line.getColor()) && Objects.equals(sections, line.sections) && Objects.equals(firstSection, line.firstSection) && Objects.equals(lastSection, line.lastSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), sections, firstSection, lastSection);
    }
}
