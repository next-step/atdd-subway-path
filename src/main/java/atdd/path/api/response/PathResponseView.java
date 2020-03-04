package atdd.path.api.response;

import atdd.station.api.response.StationResponseView;
import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class PathResponseView {

    private Long startStationId;
    private Long endStationId;
    private List<StationResponseView> stations;

    public PathResponseView(Long startStationId, Long endStationId, List<Station> stations) {
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.stations = StationResponseView.listOf(stations);
    }

}
