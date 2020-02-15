package atdd.Edge;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long lineId;
    private int elapsedTime;
    private BigDecimal distance;
    private Long sourceStationId;
    private Long targetStationId;

    @Builder
    public Edge(Long id, Long lineId, int elapsedTime, BigDecimal distance, Long sourceStationId, Long targetStationId){
        this.id = id;
        this.lineId = lineId;
        this.elapsedTime = elapsedTime;
        this.distance = distance;
        this.sourceStationId = sourceStationId;
        this.targetStationId = targetStationId;
    }

}
