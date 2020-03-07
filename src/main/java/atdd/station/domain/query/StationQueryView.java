package atdd.station.domain.query;

import atdd.station.domain.Station;
import lombok.Getter;

import java.util.List;

import static java.util.Collections.emptyList;

@Getter
public class StationQueryView {

    private Long id;
    private String name;
    private List<StationLineQueryView> lines;

    public StationQueryView() {
        this(0L, "", emptyList());
    }

    public StationQueryView(Station station, List<StationLineQueryView> lines) {
        this(station.getId(), station.getName(), lines);
    }

    public StationQueryView(Long id, String name, List<StationLineQueryView> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

}
