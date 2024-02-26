package nextstep.subway.line;

import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(final Section section) {
        sections.addSection(section);
    }
    public Line() {
    }

    public void updateLine(String name, String color) {
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

    public Long getUpStationId() {
        return sections.getFirstUpStationId();
    }

    public Long getDownStationId() {
        return sections.getLastDownStationId();
    }

    public List<Long> getAllStationId() {
        return sections.getAllStationId();
    }

    public void removeStation(Long stationId) {
        sections.removeLastSection(stationId);
    }

    public List<Section> getSectionList() {
        return sections.getSectionList();
    }
}
