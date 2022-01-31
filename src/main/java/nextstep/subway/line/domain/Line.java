package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Builder
    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = Section.builder()
                .line(this)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

        sections.add(section);
    }

    public List<Station> getAllStations() {
        return sections.getAllStation();
    }

    public void removeSection(Station station) {
        sections.remove(station);
    }
}
