package nextstep.subway.domain;

import nextstep.subway.common.AddTypeEnum;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

import static nextstep.subway.common.AddTypeEnum.FRONT_ADD_SECTION;
import static nextstep.subway.common.AddTypeEnum.MIDDLE_ADD_SECTION;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

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

    public Sections getSections() {
        return sections;
    }

    public void addSection(AddTypeEnum addTypeEnum, Section section) {
        if (FRONT_ADD_SECTION.equals(addTypeEnum)) {
            sections.addFront(section);
            return;
        }

        if (MIDDLE_ADD_SECTION.equals(addTypeEnum)) {
            sections.addMiddle(section);
            return;
        }

        sections.addBack(section);
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }
}
