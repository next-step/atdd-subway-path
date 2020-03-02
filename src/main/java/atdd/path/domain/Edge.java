package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class Edge {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long sourceId;
    private Long targetId;
    private Long lineId;

    public Edge() {
    }

    @Builder
    public Edge(Long id, Long sourceId, Long targetId, Long lineId) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.lineId = lineId;
    }
}
