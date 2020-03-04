package atdd.station.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class StationResponseView {

    private Long id;
    private String name;

    public StationResponseView(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public static List<StationResponseView> listOf(List<Station> stations) {
        return stations.stream().map(StationResponseView::new).collect(toList());
    }

}
