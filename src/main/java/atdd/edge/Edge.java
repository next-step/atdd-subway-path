package atdd.edge;

import atdd.line.Line;
import atdd.station.AbstractEntity;
import atdd.station.Station;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Edge extends AbstractEntity {

    @ManyToOne
    private Line line;

    private int elapsedTime;

    private BigDecimal distance;

    @ManyToOne
    private Station sourceStation;

    @ManyToOne
    private Station targetStation;

}
