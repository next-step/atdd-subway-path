package atdd.path.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@ToString(exclude = {"line", "sourceStation", "targetStation"})
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

    private int distance;

    @Builder
    public Edge(Long lineId, Long sourceStationId, Long targetStationId, int distance) {
        this.line = Line.builder().id(lineId).build();
        this.sourceStation = Station.builder().id(sourceStationId).build();
        this.targetStation = Station.builder().id(targetStationId).build();
        this.distance = distance;
    }

    @Builder(builderClassName = "updateBuilder", builderMethodName = "updateBuilder")
    public Edge(Long lineId, Station sourceStation, Station targetStation, int distance) {
        this.line = Line.builder().id(lineId).build();
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
        this.distance = distance;
    }

    public boolean hasStation(Station station) {
        return sourceStation.equals(station) || targetStation.equals(station);
    }

    public void updateLine(Line line) {
        this.line = line;
    }
}
