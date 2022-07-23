package nextstep.subway.line.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void addSection(Long upStationId, Long downStationId, int distance) {
        sections.add(new Section(this, upStationId, downStationId, distance));
    }

    public void removeSection(Long stationId) {
        sections.removeSection(stationId);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections.getSections());
    }

    public List<Long> stationIds() {
        return sections.stationIds();
    }
}
