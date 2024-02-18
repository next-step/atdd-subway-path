package nextstep.subway.domain.entity;

import lombok.AllArgsConstructor;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.domain.response.StationResponse;
import nextstep.subway.strategy.PathStrategy;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Path {
    private PathStrategy pathStrategy;
    private List<Station> stations;
    private int shortestDistance;

    public Path(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public PathResponse createPathResponse() {
        List<StationResponse> stationList = new ArrayList<>();
        this.stations.forEach(station -> stationList.add(station.createStationResponse()));

        return new PathResponse(stationList, this.shortestDistance);
    }

}
