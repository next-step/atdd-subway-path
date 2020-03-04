package atdd.line.domain;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private int distance;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "source_station_id", referencedColumnName = "id", nullable = false)
    private Station sourceStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "target_station_id", referencedColumnName = "id", nullable = false)
    private Station targetStation;

    @Builder
    protected Edge(Line line, int elapsedTime, int distance, Station sourceStation, Station targetStation) {
        this.line = line;
        this.elapsedTime = elapsedTime;
        this.distance = distance;
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
    }

    @Builder(builderMethodName = "testBuilder")
    protected Edge(Long id, Line line, int elapsedTime, int distance, Station sourceStation, Station targetStation) {
        this(line, elapsedTime, distance, sourceStation, targetStation);
        this.id = id;
    }

    public void changeLine(Line line) {
        this.line = line;
    }

    public Long getSourceStationId() {
        return sourceStation.getId();
    }

    public Long getTargetStationId() {
        return targetStation.getId();
    }

    public boolean hasStation(Station station) {
        return sourceStation.equals(station) || targetStation.equals(station);
    }

}
