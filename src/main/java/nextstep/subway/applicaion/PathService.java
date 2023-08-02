package nextstep.subway.applicaion;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.FindPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public Path find(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            throw new FindPathException(ErrorType.SAME_SOURCE_AND_TARGET);
        }
        try {
            Station source = stationService.findById(sourceId);
            Station target = stationService.findById(targetId);
            WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
            stationService.findAllStations().forEach(station -> graph.addVertex(station));
            lineService.findAllLines().forEach(line -> line.getSections().forEach(
                    section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance())
            ));
            DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
            GraphPath shortestPath = dijkstraShortestPath.getPath(source, target);
            if (shortestPath == null) {
                throw new FindPathException(ErrorType.NOT_EXIST_PATH);
            }
            return new Path(shortestPath.getVertexList(), (int) shortestPath.getWeight());

        } catch (IllegalArgumentException e) {
            throw new FindPathException(ErrorType.NOT_EXIST_SOURCE_AND_TARGET);
        }
    }
}
