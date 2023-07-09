package subway.domain;

import lombok.*;

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

    public void changeDistance(BigDecimal distance) {
        this.distance = distance;
    }

    //associate util method
    public void apply(StationLine line) {
        this.line = line;
    }
}
