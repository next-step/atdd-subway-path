package atdd.path.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PathRequestView {
    private Long startId;
    private Long endId;

    public PathRequestView() {
    }

    @Builder
    public PathRequestView(Long startId, Long endId) {
        this.startId = startId;
        this.endId = endId;
    }
}
