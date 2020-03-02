package atdd.path.domain.dto;

import atdd.path.domain.Edge;
import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
public class EdgeDto {
    private Long lineId;
    private Long sourceStationId;
    private Long targetStationId;
    private int distance;
    private int elapsedMinutes;

    public static EdgeDto of(Edge savedEdge) {
        return EdgeDto.builder()
                .lineId(savedEdge.getLine().getId())
                .sourceStationId(savedEdge.getSourceStation().getId())
                .targetStationId(savedEdge.getTargetStation().getId())
                .build();
    }

    public Edge toEdge() {
        return Edge.builder()
                .lineId(this.lineId)
                .sourceStationId(this.sourceStationId)
                .targetStationId(this.targetStationId)
                .distance(this.distance)
                .elapsedMinutes(this.elapsedMinutes)
                .build();
    }
}
