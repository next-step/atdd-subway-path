package atdd.path.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EdgeResponseView {
    private Long id;
    private Long sourceId;
    private Long targetId;
    private Long lineId;
    private int distance;

    public EdgeResponseView() {
    }
    @Builder
    public EdgeResponseView(Long id, Long sourceId, Long targetId, Long lineId, int distance) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
        this.distance = distance;
    }
}

