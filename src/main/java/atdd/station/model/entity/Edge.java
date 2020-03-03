package atdd.station.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Table
@Entity
@Getter
public class Edge extends BaseEntity {
    @Setter
    @Column(nullable = false)
    private long sourceStationId;

    @Column(nullable = false)
    private long targetStationId;

    public Edge() {
    }

    @Builder
    private Edge(final long sourceStationId, final long targetStationId) {
        this.sourceStationId = sourceStationId;
        this.targetStationId = targetStationId;
    }

    public boolean connectSourceStation(final long stationId) {
        if (this.sourceStationId == stationId)
            return true;

        return false;
    }

    public boolean connectTargetStation(final long stationId) {
        if (this.targetStationId == stationId)
            return true;

        return false;
    }

    public boolean connectedEdge(final Edge edge) {
        if (connectSourceStation(edge.getTargetStationId()) || connectTargetStation(edge.getSourceStationId()))
            return true;

        return false;
    }

    public List<Long> stationIds() {
        return Arrays.asList(this.sourceStationId, this.targetStationId);
    }
}
