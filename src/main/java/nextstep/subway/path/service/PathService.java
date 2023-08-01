package nextstep.subway.path.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.path.domain.PathGraph;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService,
        LineService lineService
            ) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse searchPath(Long source, Long target) {
        Station sourceStation = stationService.getStation(source);
        Station targetStation = stationService.getStation(target);

        PathGraph graph = getGraph();

        List<Station> shortestPath = graph.getShortestPath(sourceStation, targetStation);
        Double shortestDistance = graph.getDistance(sourceStation, targetStation);

        return new PathResponse(getStationResponse(shortestPath), shortestDistance);
    }

    private PathGraph getGraph() {
        List<Station> stationList = stationService.getAllStations();
        List<Line> lines = lineService.getAllLines();
        List<Section> sections = new ArrayList<>();
        lines.forEach(line -> sections.addAll(line.getSectionList()));
        return new PathGraph(stationList, sections);
    }

    private List<StationResponse> getStationResponse(List<Station> stations) {
        return stations
            .stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }
}
