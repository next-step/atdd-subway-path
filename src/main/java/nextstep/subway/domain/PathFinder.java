package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.exception.UnLinkedStationsException;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.math.BigDecimal;
import java.util.List;

public class PathFinder {
  private final List<Line> lines;

  public PathFinder(List<Line> lines) {
    this.lines = lines;
  }

  public PathResponse findShortestPath(Station sourceStation, Station targetStation) {
    WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    addVertexFromLines(graph);
    addEdges(graph);
    DijkstraShortestPath<Station, DefaultWeightedEdge> pathMap = new DijkstraShortestPath<Station, DefaultWeightedEdge>(graph);
    GraphPath<Station, DefaultWeightedEdge> shortestGraph = mapToPath(pathMap, sourceStation, targetStation);
    List<Station> stations = shortestGraph.getVertexList();
    int distance = BigDecimal.valueOf(shortestGraph.getWeight()).intValue();

    return PathResponse.of(stations, distance);
  }


  private void addVertexFromLines(AbstractBaseGraph<Station, DefaultWeightedEdge> graph) {
    lines.stream()
      .map(line -> line.getSections().getSectionStations())
      .flatMap(List::stream)
      .distinct()
      .forEach(graph::addVertex);
  }

  private void addEdges(WeightedGraph<Station, DefaultWeightedEdge> graph) {
    lines.stream()
      .flatMap(line -> line.getSections().stream())
      .forEach(section -> {
        graph.setEdgeWeight(
          graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()
        );
        graph.setEdgeWeight(
          graph.addEdge(section.getDownStation(), section.getUpStation()), section.getDistance()
        );
      });
  }

  private GraphPath<Station, DefaultWeightedEdge> mapToPath(DijkstraShortestPath<Station, DefaultWeightedEdge> pathMap, Station sourceStation, Station targetStation) {
    GraphPath<Station, DefaultWeightedEdge> path = pathMap.getPath(sourceStation, targetStation);

    // DijkstraShortestPath 에서 연결되지 않은 두 점을 이을 경우 nullPointerException이 나서 null로 처리했습니다...
    if (path == null) {
      throw new UnLinkedStationsException();
    }

    if (path.getWeight() <= 0){
      throw new UnLinkedStationsException();
    }
    return path;
  }
}
