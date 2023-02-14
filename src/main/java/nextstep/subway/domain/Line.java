package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private SectionCollection sectionCollection = new SectionCollection();

    public Line() {
    }

    public Line(String name, String color) {
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

    public SectionCollection getSectionCollection() {
        return sectionCollection;
    }

    public void addSections(Section section) {
        sectionCollection.addSectionCollection(section);
    }

    public List<Station> getStations() {

        return sectionCollection.getStations();
    }

    public void removeStation(Station station) {
        sectionCollection.removeSectionCollection(station);
    }
}
