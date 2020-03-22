package atdd.path.domain;

import atdd.path.dto.EdgeRequestView;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
public class Edge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sourceId;
    private Long targetId;
    private Long lineId;
    private int distance;

    @Builder
    public Edge(Long id, Long sourceId, Long targetId, Long lineId, int distance) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
        this.distance = distance;
    }


    public static Edge of(EdgeRequestView requestView) {
        return Edge.builder()
                .lineId(requestView.getLineId())
                .sourceId(requestView.getSourceId())
                .targetId(requestView.getTargetId())
                .distance(requestView.getDistance())
                .build();
    }
}
