package atdd.path.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PathRequestView {
    private Long startId;
    private Long endId;

    @Builder
    public PathRequestView(Long startId, Long endId) {
        this.startId = startId;
        this.endId = endId;
    }
}
