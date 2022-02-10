package nextstep.subway.domain;

import java.util.List;

import javax.persistence.*;

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

    public void addSection(Section section) {
        sections.add(section);
    }

    public int lastIndexOfSections() {
        return sections.size() - 1;
    }

    public Section sectionByIndex(int index) {
        return sections.findByIndex(index);
    }

    public void removeSectionByIndex(int index) {
        sections.removeByIndex(index);
    }

    public List<Section> allSections(){
        return sections.findAll();
    }

    public List<Station> allStations() {
        return sections.findAllStations();
    }
}
