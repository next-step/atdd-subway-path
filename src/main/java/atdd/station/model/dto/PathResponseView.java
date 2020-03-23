package atdd.station.model.dto;

import atdd.station.model.entity.Station;
import lombok.Getter;

import java.util.List;

@Getter
public class PathResponseView {
    private long startStationId;
    private long endStationId;
    private List<StationSimpleDto> stations;

    public PathResponseView() {
    }

    private PathResponseView(long startStationId, long endStationId, List<StationSimpleDto> stations) {
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.stations = stations;
    }

    public static PathResponseView of(long startId, long endId, List<Station> stations) {
        return new PathResponseView(
                startId,
                endId,
                StationSimpleDto.listOf(stations));
    }
}
