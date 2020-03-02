package atdd.station.model.dto;

import atdd.station.model.entity.Edge;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEdgeRequestView {
    private long sourceStationId;
    private long targetStationId;

    public Edge toEdge() {
        return Edge.builder()
                .sourceStationId(this.sourceStationId)
                .targetStationId(this.targetStationId)
                .build();
    }
}
