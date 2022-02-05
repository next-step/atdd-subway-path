package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(final StationRepository stationRepository, final LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public void findShortestPath(final Long sourceId, final Long targetId) {
        final Station sourceStation = stationRepository.findById(sourceId).orElseThrow((IllegalArgumentException::new));
        final Station targetStation = stationRepository.findById(targetId).orElseThrow((IllegalArgumentException::new));
        if (sourceStation.isSameName(targetStation)) {
            throw new IllegalArgumentException("source and target stations were conflict");
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);


        // 노드 입력 -- 각각의 역을 입력해야겠지?
        stationRepository.findAll().stream()
                .forEach(graph::addVertex);

//        final List<Line> all = lineRepository.findAll();
//        all.stream().forEach(it -> it.getSections());
//
//        // 간선 입력
//        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
//        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 3);
//        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);
//        graph.setEdgeWeight(graph.addEdge("v2", "v4"), 1);
//        graph.setEdgeWeight(graph.addEdge("v3", "v4"), 1);


        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();

    }
}
