package nextstep.subway.domain.line.entity;

import lombok.*;
import nextstep.subway.domain.station.entity.Station;

import javax.persistence.*;

@Entity
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station downStation;

    private Long distance;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    public static Section of(Station upStation, Station downStation, Long distance) {
        return new Section(null, upStation, downStation, distance, null);
    }
}
