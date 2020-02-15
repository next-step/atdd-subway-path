package atdd.station.dto.path;

import atdd.station.domain.Station;

import java.util.List;

public class PathFindResponseDto {
    private String startStationId;
    private String endStationId;
    private List<Station> stations;

    public PathFindResponseDto(String startStationId, String endStationId, List<Station> stations) {
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.stations = stations;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public String getEndStationId() {
        return endStationId;
    }

    public List<Station> getStations() {
        return stations;
    }
}
