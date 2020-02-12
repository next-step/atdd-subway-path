package atdd.station;

import javax.persistence.*;

@Entity
public class Edge {
    @EmbeddedId
    EdgeKey id;

    @ManyToOne
    @MapsId("line_id")
    @JoinColumn(name = "station_id")
    Station sourceStation;

    @ManyToOne
    @MapsId("line_id")
    @JoinColumn(name = "station_id")
    Station targetStation;

    @ManyToOne
    @MapsId("station_id")
    @JoinColumn(name = "line_id")
    Line line;

    int distance;
    int elapseTime;


    public Station getSourceStation() {
        return sourceStation;
    };

    public Station getTargetStation() {
        return targetStation;
    };

    public Line getLine() {
        return line;
    }
}
