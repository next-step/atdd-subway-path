package atdd.station.api.response;

import atdd.station.repository.Station;

import java.util.List;
import java.util.StringJoiner;

public class FindStationResponse {

    private int count;
    private List<Station> stations;

    protected FindStationResponse() {}

    public FindStationResponse(int count, List<Station> stations) {
        this.count = count;
        this.stations = stations;
    }

    public int getCount() {
        return count;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FindStationResponse.class.getSimpleName() + "[", "]")
                .add("count=" + count)
                .add("stations=" + stations)
                .toString();
    }

}
