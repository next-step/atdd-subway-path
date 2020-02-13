package atdd.Edge;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class EdgeCreateRequest {
    private Long lineId;
    private int elapsedTime;
    private BigDecimal distance;
    private Long sourceStationId;
    private Long targetStationId;

    @Builder
    public EdgeCreateRequest(Long lineId, int elapsedTime, BigDecimal distance, Long sourceStationId, Long targetStationId){
        this.lineId = lineId;
        this.elapsedTime = elapsedTime;
        this.distance = distance;
        this.sourceStationId = sourceStationId;
        this.targetStationId = targetStationId;
    }

    public Edge toEntity(){
        return Edge.builder()
                .lineId(lineId)
                .elapsedTime(elapsedTime)
                .distance(distance)
                .sourceStationId(sourceStationId)
                .targetStationId(targetStationId)
                .build();
    }
}
