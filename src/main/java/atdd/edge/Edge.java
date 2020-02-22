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
import java.util.HashSet;
import java.util.Set;

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

    public Set<Station> getStations() {
        Set stations = new HashSet();
        stations.add(sourceStation);
        stations.add(targetStation);
        return stations;
    }

}
