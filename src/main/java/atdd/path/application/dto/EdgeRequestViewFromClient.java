package atdd.path.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.jackson.JsonComponent;

@Getter
@JsonComponent
public class EdgeRequestViewFromClient {
    private Long lineId;
    private Long sourceId;
    private Long targetId;
    private int distance;
    private int timeToTake;

    public EdgeRequestViewFromClient() {
    }

    @Builder
    public EdgeRequestViewFromClient(Long lineId, Long sourceId, Long targetId, int distance, int timeToTake) {
        this.lineId = lineId;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.distance = distance;
        this.timeToTake = timeToTake;
    }
}
