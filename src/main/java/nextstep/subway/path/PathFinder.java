package nextstep.subway.path;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.Station;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    public PathResponse getPath(List<Sections> sectionsList, Station sourceStation, Station targetStation) {
        try {
            WeightedMultigraph<Station, DefaultWeightedEdge> graph = initGraph(sectionsList);

            GraphPath path = getShortestPath(graph, sourceStation, targetStation);

            List<Station> stations = path.getVertexList();
            int distance = (int) path.getWeight();

            return new PathResponse(stations.stream().map(StationResponse::ofEntity).collect(Collectors.toList()), distance);

        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new PathException("연결되어있지 않은 출발역과 도착역의 경로는 조회할 수 없습니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> initGraph(List<Sections> sectionsList) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (Sections sections : sectionsList) {
            for (Section section : sections.getSections()) {
                addStations(graph, section);
                addEdge(graph, section);
            }
        }

        return graph;
    }

    private void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
        graph.setEdgeWeight(edge, section.getDistance());
    }

    private void addStations(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.addVertex(section.getUpStation());
        graph.addVertex(section.getDownStation());
    }

    private GraphPath getShortestPath(
            WeightedMultigraph<Station, DefaultWeightedEdge> graph,
            Station sourceStation,
            Station targetStation
    ) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return dijkstraShortestPath.getPath(sourceStation, targetStation);
    }
}
