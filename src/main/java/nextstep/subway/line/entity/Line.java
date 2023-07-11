package nextstep.subway.line.entity;

import lombok.*;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;

import javax.persistence.*;
import java.util.Collections;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    @Builder
    public Line(
            String name,
            String color,
            Station upStation,
            Station downStation,
            Integer distance
    ) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(
                Collections.singletonList(Section.builder()
                        .upStation(upStation)
                        .downStation(downStation)
                        .distance(distance)
                        .build()),
                distance
        );
    }

    public void updateLine(Line updateLine) {
        this.name = updateLine.name;
        this.color = updateLine.color;
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
    }

    public void deleteSectionByStationId(Long stationId) {
        this.sections.deleteSectionByStationId(stationId);
    }
}
