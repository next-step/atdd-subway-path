package nextstep.subway.map.domain;

import nextstep.subway.line.domain.LineStation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShortestPath {
    List<Long> shortestPathIds;

    public ShortestPath(List<Long> shortestPathIds) {
        this.shortestPathIds = shortestPathIds;
    }

    List<LineStation> toLineStations(Map<Long, LineStation> lineStationsWithId) {
        return shortestPathIds.stream()
                .map(lineStationsWithId::get)
                .collect(Collectors.toList());
    }
}
