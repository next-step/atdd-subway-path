package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    //    private List<Section> sections = new ArrayList<>();
    private Sections sections = new Sections();

    @Column
    private long distance;

    public Line() {}

    public Line(String name, String color, long distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void updateLine(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
    }

    public void validateSaveSection(Section section) {
        sections.validateSaveSection(section);
    }

    public void validateDeleteSection(Station station) {
        sections.validateDeleteSection(station);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                ", distance=" + distance +
                '}';
    }
}
