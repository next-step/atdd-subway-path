package atdd.station.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class FindStationsResponse {

    private int count;
    private List<Station> stations;

    public FindStationsResponse(int count, List<Station> stations) {
        this.count = count;
        this.stations = stations;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FindStationsResponse.class.getSimpleName() + "[", "]")
                .add("count=" + count)
                .add("stations=" + stations)
                .toString();
    }

}
