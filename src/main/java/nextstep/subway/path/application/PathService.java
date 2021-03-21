package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private LineService lineService;
    private StationService stationService;
    private GraphService graphService;

    public PathService(LineService lineService, StationService stationService, GraphService graphService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.graphService = graphService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);

        SubwayGraph subwayGraph = graphService.findGraph();
        subwayGraph.initialize(findSections());

        return createPathResponse(subwayGraph, source, target);
    }

    private List<Section> findSections() {
        return lineService.findLines().stream()
                .map(it -> it.getSections())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private PathResponse createPathResponse(SubwayGraph subwayGraph, Station source, Station target) {
        Path path = subwayGraph.getShortestPath(source, target);

        List<StationResponse> stationResponses = path.getStations().stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, path.getDistance());
    }
}
