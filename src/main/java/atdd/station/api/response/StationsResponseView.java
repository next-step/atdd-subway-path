package atdd.station.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class StationsResponseView {

    private int count;
    private List<StationResponseView> stations;

    public StationsResponseView(List<StationResponseView> stations) {
        this.count = stations.size();
        this.stations = stations;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StationsResponseView.class.getSimpleName() + "[", "]")
                .add("count=" + count)
                .add("stations=" + stations)
                .toString();
    }

}
