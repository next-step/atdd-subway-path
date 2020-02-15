package atdd.Edge;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class EdgeResponse {
    private Long id;
    private Long lineId;
    private int elapsedTime;
    private BigDecimal distance;
    private Long sourceStationId;
    private Long targetStationId;

    public EdgeResponse(){

    }

    @Builder
    public EdgeResponse(Edge entity){
        this.id = entity.getId();
        this.lineId = entity.getLineId();
        this.elapsedTime = entity.getElapsedTime();
        this.distance = entity.getDistance();
        this.sourceStationId = entity.getSourceStationId();
        this.targetStationId = entity.getTargetStationId();
    }

    public static EdgeResponse of(Edge entity){
        return EdgeResponse.builder()
                .entity(entity)
                .build();
    }
}
