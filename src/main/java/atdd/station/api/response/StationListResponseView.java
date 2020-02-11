package atdd.station.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class StationListResponseView {

    private int count;
    private List<StationResponseView> stations;

    public StationListResponseView(int count, List<StationResponseView> stations) {
        this.count = count;
        this.stations = stations;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StationListResponseView.class.getSimpleName() + "[", "]")
                .add("count=" + count)
                .add("stations=" + stations)
                .toString();
    }

}
