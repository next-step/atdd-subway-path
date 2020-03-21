package atdd.path.dto;

import atdd.path.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PathResponseView {
    private Long startStationId;
    private Long endStationId;
    private List<Station> stations = new ArrayList<>();

    @Builder
    public PathResponseView(Long startStationId, Long endStationId, List<Station> stations) {
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.stations = stations;
    }
}
