package nextstep.subway.domain;

import lombok.Builder;
import nextstep.subway.domain.object.Distance;
import nextstep.subway.domain.object.Sections;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames= { "name" }
                )
        })
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    public Line() {
    }

    @Builder
    public Line(String name, String color, Station upStation, Station downStation, Distance distance) {
        this.name = name;
        this.color = color;

        addSection(upStation, downStation, distance);
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

    public void addSection(Station upStation, Station downStation, Distance distance) {
        Section section = Section.builder()
                .line(this)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

        sections.add(section);
    }

    public List<Station> getAllStations() {
        return this.sections.getAllStations();
    }

    public void removeSection(Long stationId) {
        this.sections.removeSection(stationId);
    }

    public void update(String name, String color) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }

        if (color != null && !color.isEmpty()) {
            this.color = color;
        }
    }
}
