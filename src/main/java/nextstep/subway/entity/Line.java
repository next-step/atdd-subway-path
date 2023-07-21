package nextstep.subway.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.entity.group.SectionGroup;

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
    private SectionGroup sections;

    public Line() {}

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new SectionGroup();
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

    public SectionGroup getSections() {
        return sections;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public Section addSection(Station upStation, Station downStation, int distance) {
        return sections.add(this, upStation, downStation, distance);
    }

    public List<Station> getStations() {
        return sections.getStationsInOrder();
    }

    public void removeSection(final Section section) {

        sections.delete(section.getDownStationId());

    }
}
