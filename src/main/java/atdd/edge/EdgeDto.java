package atdd.edge;

import atdd.line.Line;
import atdd.station.Station;
import lombok.Getter;

import java.math.BigDecimal;

public class EdgeDto {

    @Getter
    public static class Request {

        private Long lineId;
        private int elapsedTime;
        private BigDecimal distance;
        private Long sourceStationId;
        private Long targetStationId;

        Edge toEntity(Line line, Station sourceStation, Station targetStation) {
            return Edge.builder()
                    .line(line)
                    .elapsedTime(elapsedTime)
                    .distance(distance)
                    .sourceStation(sourceStation)
                    .targetStation(targetStation)
                    .build();
        }
    }
}
