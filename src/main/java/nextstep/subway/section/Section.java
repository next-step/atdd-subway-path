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

    public static Section initSection(Line line, Station upstation, Station downstation) {
        Section section = Section.builder()
                .line(line)
                .upstation(upstation)
                .downstation(downstation)
                .distance(line.getDistance())
                .build();

        //sectionRepository.save(section);
        return section;
    }
}
