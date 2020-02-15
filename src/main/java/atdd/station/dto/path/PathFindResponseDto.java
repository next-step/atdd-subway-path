package atdd.station.dto.path;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class PathFindResponseDto {
    private long startStationId;
    private long endStationId;
    private List<Station> stations;

    @Builder
    public PathFindResponseDto(long startStationId, long endStationId, List<Station> stations) {
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.stations = stations;
    }

    public long getStartStationId() {
        return startStationId;
    }

    public long getEndStationId() {
        return endStationId;
    }

    public List<Station> getStations() {
        return stations;
    }
}
