package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {
    private final LineService lineService;
    private final StationService stationService;


    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    private PathFinder initializePathFinder() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        List<Line> lines = lineService.findLines();

        for(Line line : lines) {
            List<Station> stations = line.getStations();
            List<Section> sections = line.getSections();

            for(Station station : stations) {
                graph.addVertex(station);
            }

            for(Section section : sections) {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }

        return new PathFinder(graph);
    }

    public PathResponse getShortestPath(Long sourceId, Long targetId) {
        PathFinder pathFinder = initializePathFinder();
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);
        GraphPath path = pathFinder.getShortestPath(source, target);
        return createPathResponse(path);
    }

    private PathResponse createPathResponse(GraphPath path) {
        List<StationResponse> stations = (List<StationResponse>) path.getVertexList().stream()
                .map(station -> StationResponse.of((Station) station))
                .collect(Collectors.toList());

        return PathResponse.of(stations, path.getWeight());
    }
}
