package subway.domain;

import lombok.*;
import subway.exception.StationLineSectionSplitException;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Entity
@EqualsAndHashCode(of = "sectionId")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationLineSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private StationLine line;

    @JoinColumn(name = "up_station_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @JoinColumn(name = "down_station_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @Column
    private BigDecimal distance;

    @Builder
    public StationLineSection(Station upStation, Station downStation, BigDecimal distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void splitSection(Station standardStation, Station newStation, BigDecimal newSectionDistance) {
        this.distance = distance.subtract(newSectionDistance);

        if (this.distance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StationLineSectionSplitException("can't split existing section into larger distance section");
        }

        if (standardStation.equals(upStation)) {
            this.upStation = newStation;
            return;
        }
        this.downStation = newStation;
    }

    //associate util method
    public void apply(StationLine line) {
        this.line = line;
    }
}
