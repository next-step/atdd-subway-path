package atdd.path.dto;

import lombok.Builder;

public class EdgeRequestView {
    private Long sourceId;
    private Long targetId;
    private Long lineId;
    private int distance;

    public EdgeRequestView() {
    }

    @Builder
    public EdgeRequestView(Long sourceId, Long targetId, Long lineId, int distance) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
        this.distance = distance;
    }
}
