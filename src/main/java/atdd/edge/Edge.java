package atdd.edge;

import atdd.line.Line;
import atdd.station.domain.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@ToString(exclude = { "line", "sourceStation", "targetStation"})
@NoArgsConstructor
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "source_station_id")
    private Station sourceStation;

    @JsonIgnore
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
