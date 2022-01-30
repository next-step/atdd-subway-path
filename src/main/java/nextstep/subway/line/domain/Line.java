package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import nextstep.subway.common.domain.model.BaseEntity;
import nextstep.subway.station.domain.Station;

@Getter
@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    private Long id;

    @Column(
        name = "NAME",
        nullable = false,
        unique = true,
        length = 50
    )
    private String name;

    @Column(
        name = "COLOR",
        nullable = false,
        length = 50
    )
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public List<Station> getStations() {
        return sections.toStations();
    }

    public void edit(String name, String color) {
        if (Objects.nonNull(name)) {
            this.name = name;
        }
        if (Objects.nonNull(color)) {
            this.color = color;
        }
    }

    public void addSection(Station upStation, Station downStation, Distance distance) {
        Section section = Section.builder()
            .line(this)
            .upStation(upStation)
            .downStation(downStation)
            .distance(distance)
            .build();
        this.sections.add(section);
    }

    public void deleteSection(Long stationId) {
        this.sections.remove(stationId);
    }

    public int getLength() {
        return sections.totalDistance();
    }
}
