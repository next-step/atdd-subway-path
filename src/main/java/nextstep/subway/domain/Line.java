package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Embedded
    private final Sections sections = new Sections();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    protected Line() {
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

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void deleteLastSection() {
        sections.deleteLastSection();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void initSection(Section section) {
        sections.initSection(section);
    }

    public void deleteSection(Long stationId) {
        Section downSection = null;
        Section upSection = null;
        if (getSections().stream().filter(section -> section.getUpStation().getId() == stationId).findFirst().isPresent()) {
            downSection = getSections().stream().filter(section -> section.getUpStation().getId() == stationId).findFirst().get();
            sections.getSections().remove(downSection);
        }
        if (getSections().stream().filter(section -> section.getDownStation().getId() == stationId).findFirst().isPresent()) {
            upSection = getSections().stream().filter(section -> section.getUpStation().getId() == stationId).findFirst().get();
            sections.getSections().remove(upSection);
        }
    }
}
