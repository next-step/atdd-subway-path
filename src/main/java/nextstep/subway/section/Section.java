package nextstep.subway.section;

import lombok.*;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.Objects;

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
    @Setter
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
        return downstation.equals(section.getDownstation()) ||
                upstation.equals(section.getUpstation());
    }

    public boolean isUpstation(Station station) {
        return upstation.equals(station);
    }

    public boolean isDownstation(Station station) {
        return downstation.equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        if (id != null && section.id != null) {
            return Objects.equals(id, section.id);
        } else {
            return Objects.equals(upstation, section.upstation) &&
                    Objects.equals(downstation, section.downstation);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id != null ? id : upstation, downstation);
    }
}
