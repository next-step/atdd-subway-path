package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEdgeRequestView {
    private long sourceStationId;
    private long targetStationId;
    private int elapsedTime;
    private int distance;

    public Edge toEdge() {
        return new Edge(sourceStationId, targetStationId, elapsedTime, distance);
    }
}
