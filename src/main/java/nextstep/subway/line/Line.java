package nextstep.subway.line;

import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line() {}

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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSectionList() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void changeLineInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addMiddleSection(Section newSection) {
        newSection.registerLine(this);
    }

    public void addEndSection(Section newSection) {
        sections.validateSection(newSection);
        newSection.registerLine(this);
    }

    public Long deleteDownSection(Station deleteStation) {
        return sections.deleteDownStation(deleteStation);
    }

    public Section createNewSection(Section existingSection, Section requestSection) {
        return sections.createNewSection(existingSection, requestSection);
    }

    public void removeSection(Section section) {
        sections.removeSection(section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName()) && Objects.equals(getColor(), line.getColor()) && Objects.equals(getSectionList(), line.getSectionList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), getSectionList());
    }
}
