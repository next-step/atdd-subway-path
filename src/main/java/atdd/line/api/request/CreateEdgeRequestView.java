package atdd.line.api.request;

import atdd.line.domain.Edge;
import atdd.line.domain.Line;
import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class CreateEdgeRequestView {

    private int elapsedTime;
    private int distance;
    private Long sourceStationId;
    private Long targetStationId;

    public Edge toEdge(Line line, Station sourceStation, Station targetStation) {
        return Edge.builder()
                .line(line)
                .elapsedTime(elapsedTime)
                .distance(distance)
                .sourceStation(sourceStation)
                .targetStation(targetStation)
                .build();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateEdgeRequestView.class.getSimpleName() + "[", "]")
                .add("elapsedTime=" + elapsedTime)
                .add("distance=" + distance)
                .add("sourceStationId=" + sourceStationId)
                .add("targetStationId=" + targetStationId)
                .toString();
    }

}
