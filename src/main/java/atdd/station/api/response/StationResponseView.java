package atdd.station.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class StationResponseView {

    private Long id;
    private String name;
    private List<StationLineResponse> lines;

    public StationResponseView(Station station) {
        this(station, emptyList());
    }

    public StationResponseView(Station station, List<StationLineResponse> lines) {
        this.id = station.getId();
        this.name = station.getName();
        this.lines = lines;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StationResponseView.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("lines=" + lines)
                .toString();
    }

}
