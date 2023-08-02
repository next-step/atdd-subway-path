package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import lombok.Getter;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Getter
    private String color;

    @Embedded
    private Sections sections;

    public Line() {}

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
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

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public int getTotalDistance() {
        return sections.getTotalDistance();
    }

    public Section addSection(Section section) {
        return this.sections.addSection(section);
    }

    public List<Station> getStations() {
        return this.sections.getStationsOfSection();
    }

    public void removeSection(Station station) {
        sections.removeSection(this, station);
    }
}
