package nextstep.subway.domain;

import nextstep.subway.common.AddTypeEnum;
import nextstep.subway.exception.SubwayRestApiException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.AddTypeEnum.FRONT_ADD_SECTION;
import static nextstep.subway.common.AddTypeEnum.MIDDLE_ADD_SECTION;
import static nextstep.subway.exception.ErrorResponseEnum.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();
//    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
//    private List<Section> sections = new ArrayList<>();

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
