package atdd.path.application.dto;

import atdd.path.domain.Edge;
import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PathResponseView {
    private Long startId;
    private Long endId;
    private List<Station> stations;

    public PathResponseView() {
    }

    @Builder
    public PathResponseView(Long startId, Long endId, List<Station> stations) {
        this.startId = startId;
        this.endId = endId;
        this.stations = stations;
    }
}
