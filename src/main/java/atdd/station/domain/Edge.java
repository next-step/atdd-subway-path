package atdd.station.domain;

import lombok.Builder;

import javax.persistence.*;

@Entity
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "station_source_id")
    private Station sourceStation;

    @OneToOne
    @JoinColumn(name = "station_target_id")
    private Station targetStation;

    @ManyToOne
    @JoinColumn(name = "subway_line_id")
    private SubwayLine subwayLine;

    private int distance;

    private boolean deleted = false;

    public Edge() {
    }

    @Builder
    public Edge(Station sourceStation, Station targetStation, SubwayLine subwayLine, int distance) {
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
        this.subwayLine = subwayLine;
        this.distance = distance;
    }

    public SubwayLine getSubwayLine() {
        return subwayLine;
    }

    public long getId() {
        return id;
    }

    public long getSourceStationId() {
        return sourceStation.getId();
    }

    public long getTargetStationId() {
        return targetStation.getId();
    }

    public Station getSourceStation() {
        return sourceStation;
    }

    public Station getTargetStation() {
        return targetStation;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
