package atdd.station;

import javax.persistence.*;

@Entity
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long sourceStationId;
    private Long targetStationId;
    private Long lineId;

    private double distance;
    private int elapseTime;

    protected Edge() {
    }

    public Edge(Long id, Long sourceStationId, Long targetStationId, Long lineId, double distance, int elapseTime) {
        this.id = id;
        this.sourceStationId = sourceStationId;
        this.targetStationId = targetStationId;
        this.lineId = lineId;
        this.distance = distance;
        this.elapseTime = elapseTime;
    }

    public Long getSourceStationId() {
        return sourceStationId;
    }

    public Long getId() {
        return id;
    }

    public Long getTargetStationId() {
        return targetStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public double getDistance() {
        return distance;
    }

    public int getElapseTime() {
        return elapseTime;
    }
}
