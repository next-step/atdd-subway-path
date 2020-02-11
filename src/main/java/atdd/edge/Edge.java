package atdd.edge;

import atdd.line.Line;
import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "source_station_id")
    private Station sourceStation;

    @ManyToOne
    @JoinColumn(name = "target_station_id")
    private Station targetStation;

    @Builder
    public Edge(Long lineId, Long sourceStationId, Long targetStationId) {
        this.line = Line.builder().id(lineId).build();
        this.sourceStation = Station.builder().id(sourceStationId).build();
        this.targetStation = Station.builder().id(targetStationId).build();
    }
}
