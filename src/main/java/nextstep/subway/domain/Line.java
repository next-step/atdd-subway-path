package nextstep.subway.domain;

import nextstep.subway.domain.policy.AddSectionPolicies;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    public Line() {
    }

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Sections getSections() {
        return sections;
    }

    public boolean hasSections() {
        return !sections.isEmpty();
    }

    public void addSection(Section section, AddSectionPolicies policies) {
        sections.addSection(section, policies);
    }

    public int getSectionsCount() {
        return sections.getSectionsCount();
    }

    public List<Section> getAllSections() {
        return sections.getAllSections();
    }

    public Section getFirstSection() {
        return sections.getFirstSection();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station, AddSectionPolicies policies) {
        sections.removeSection(station, policies);
    }

    public int getLength() {
        return sections.getTotalDistance();
    }

}
