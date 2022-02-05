package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;

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

    public void validateNewSection(Long upStationId, Long downStationId) {
        sections.validateNew(upStationId, downStationId);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void edit(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void removeSection(Long stationId) {
        sections.remove(stationId);
    }

    public List<Station> getStations() {
        return sections.getSortedStations();
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

    public Sections getSections() {
        return sections;
    }
}
