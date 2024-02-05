package nextstep.subway.section;

import lombok.*;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Line line;

    @ManyToOne
    @Setter
    private Station upstation;

    @ManyToOne
    @Setter
    private Station downstation;

    @Setter
    private int distance;

    public static Section initSection(Line line, Station upstation, Station downstation, int distance) {
        return Section.builder()
                .line(line)
                .upstation(upstation)
                .downstation(downstation)
                .distance(distance)
                .build();
    }

    public boolean isInSection(Section section) {
        return downstation.getId().equals(section.getDownstation().getId()) ||
                upstation.getId().equals(section.getUpstation().getId());
    }

    public boolean isUpstation(Station station) {
        return upstation.getId().equals(station.getId());
    }

    public boolean isDownstation(Station station) {
        return downstation.getId().equals(station.getId());
    }
}
