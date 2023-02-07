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

    @Column(name = "upStationId")
    private Long upStationId;

    @Column(name = "downStationId")
    private Long downStationId;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;

        this.upStationId = upStation.getId();
        this.downStationId = downStation.getId();

        this.sections.createInitialLineSection(upStation, downStation, distance, this);
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

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections.getList();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.addSection(this, upStation, downStation, distance);
        this.downStationId = downStation.getId();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        Station newDownStation = this.sections.remove(station, downStationId);
        this.downStationId = newDownStation.getId();
    }
}
