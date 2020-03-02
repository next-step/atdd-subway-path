package atdd.path.application.dto;

import atdd.path.domain.Edge;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EdgeRequestView {
    private Long id;
    private Long sourceId;
    private Long targetId;
    private Long lineId;

    public EdgeRequestView() {
    }

    @Builder
    public EdgeRequestView(Long id, Long sourceId, Long targetId, Long lineId) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
    }

    public static Edge of(EdgeRequestView requestView){
        return Edge.builder()
                .lineId(requestView.lineId)
                .sourceId(requestView.sourceId)
                .targetId(requestView.targetId)
                .build();
    }
}
