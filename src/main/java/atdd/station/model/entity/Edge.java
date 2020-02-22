package atdd.station.model.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

@Table
@Entity
@Getter
public class Edge extends BaseEntity {
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

    public boolean connectSourceAndTarget(final Edge edge) {
        if (this.sourceStationId == edge.getTargetStationId())
            return true;

        return false;
    }

    public boolean connectTargetAndSource(final Edge edge) {
        if (this.targetStationId == edge.getSourceStationId())
            return true;

        return false;
    }

    public boolean connectedEdge(final Edge edge) {
        if (connectSourceAndTarget(edge) || connectTargetAndSource(edge))
            return true;

        return false;
    }

    public List<Long> stationIds() {
        return Arrays.asList(this.sourceStationId, this.targetStationId);
    }
}
