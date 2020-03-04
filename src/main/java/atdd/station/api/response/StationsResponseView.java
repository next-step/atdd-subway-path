package atdd.station.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class StationsResponseView {

    private int count;
    private List<StationResponseView> stations;

    public StationsResponseView(List<Station> stations) {
        this.count = stations.size();
        this.stations = StationResponseView.listOf(stations);
    }

}
