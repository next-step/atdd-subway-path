package atdd.path.dto;

import atdd.path.domain.Edge;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EdgeResponseView {
    private Long id;
    private Long sourceId;
    private Long targetId;
    private Long lineId;
    private int distance;

    @Builder
    public EdgeResponseView(Long id, Long sourceId, Long targetId, Long lineId, int distance) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public static EdgeResponseView of(Edge edge){
        return EdgeResponseView.builder()
                .id(edge.getId())
                .lineId(edge.getLineId())
                .sourceId(edge.getSourceId())
                .targetId(edge.getTargetId())
                .distance(edge.getDistance())
                .build();
    }
}

