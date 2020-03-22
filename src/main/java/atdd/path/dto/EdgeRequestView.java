package atdd.path.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EdgeRequestView {
    private Long sourceId;
    private Long targetId;
    private Long lineId;
    private int distance;

    @Builder
    public EdgeRequestView(Long sourceId, Long targetId, Long lineId, int distance) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
        this.distance = distance;
    }
}
