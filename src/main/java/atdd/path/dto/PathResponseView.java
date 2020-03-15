package atdd.path.dto;

import atdd.path.domain.Station;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PathResponseView {
    private Long startStationId;
    private Long endStationId;
    private List<Station> stations = new ArrayList<>();

    public PathResponseView() {
    }

    public PathResponseView(Long startStationId, Long endStationId, List<Station> stations) {
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.stations = stations;
    }
}
