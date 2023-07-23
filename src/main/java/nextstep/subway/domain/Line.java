package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    private Long finalUpStationId;
    private Long finalDownStationId;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color, Long finalUpStationId, Long finalDownStationId) {
        this.name = name;
        this.color = color;
        this.finalUpStationId = finalUpStationId;
        this.finalDownStationId = finalDownStationId;
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

    public Long getFinalUpStationId() {
        return finalUpStationId;
    }

    public Long getFinalDownStationId() {
        return finalDownStationId;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
        section.updateLine(this);
    }

    public void updateFinalUpStationId(Long finalUpStationId) {
        this.finalUpStationId = finalUpStationId;
    }

    public void updateFinalDownStationId(Long finalDownStationId) {
        this.finalDownStationId = finalDownStationId;
    }
}
