package nextstep.subway.applicaion;

import nextstep.subway.domain.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

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
        final List<Line> all = lineRepository.findAll();
        all.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(graph::addVertex);

        all.stream()
                .flatMap(it -> it.getSections().stream())
                .forEach(it -> setEdgeWeight(graph, it));

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
    }

    private void setEdgeWeight(final WeightedMultigraph<Station, DefaultWeightedEdge> graph, final Section it) {
        graph.setEdgeWeight(graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance());
    }
}
