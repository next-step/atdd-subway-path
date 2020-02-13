package atdd.line.domain;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.StringJoiner;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Edge {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "line_id", referencedColumnName = "id", nullable = false)
    private Line line;

    @Column(nullable = false)
    private int elapsedTime;

    @Column(nullable = false)
    private BigDecimal distance;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "source_station_id", referencedColumnName = "id", nullable = false)
    private Station sourceStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "target_station_id", referencedColumnName = "id", nullable = false)
    private Station targetStation;

    @Builder
    protected Edge(Line line, int elapsedTime, BigDecimal distance, Station sourceStation, Station targetStation) {
        this.line = line;
        this.elapsedTime = elapsedTime;
        this.distance = distance;
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
    }

    public void changeTargetStation(Station targetStation) {
        this.targetStation = targetStation;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Edge.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("elapsedTime=" + elapsedTime)
                .add("distance=" + distance)
                .toString();
    }

}
