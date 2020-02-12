package atdd.station;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EdgeKey implements Serializable {

    @Column(name = "line_id")
    Long lineId;

    @Column(name = "station_id")
    Long stationId;
}
