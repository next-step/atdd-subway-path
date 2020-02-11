package atdd.edge;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
public class EdgeDto {
    private Long lineId;
    private Long sourceStationId;
    private Long targetStationId;

    public static EdgeDto of(Edge savedEdge) {
        return EdgeDto.builder()
                .lineId(savedEdge.getLineId())
                .sourceStationId(savedEdge.getSourceStationId())
                .targetStationId(savedEdge.getTargetStationId())
                .build();
    }

    public Edge toEdge() {
        return Edge.builder()
                .lineId(this.lineId)
                .sourceStationId(this.sourceStationId)
                .targetStationId(this.targetStationId)
                .build();
    }
}
