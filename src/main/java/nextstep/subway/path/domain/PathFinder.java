package nextstep.subway.path.domain;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.InvalidStationPathException;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

  private List<Sections> sectionsList;
  private WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(
      DefaultWeightedEdge.class);
  private final int MIN = 2;

  public PathFinder(List<Sections> sectionsList) {
    this.sectionsList = sectionsList;
  }

  public static PathFinder of(List<Sections> sectionsList) {
    return new PathFinder(sectionsList);
  }

  public Path findPath(Station source, Station target) {
    validate(source, target);
    DijkstraShortestPath<Station, Long> dijkstraShortestPath = createDijkstraShortestPath(
        sectionsList, source);
    GraphPath<Station, Long> graphPath = dijkstraShortestPath.getPath(source, target);
    if (graphPath == null) {
      throw new InvalidStationPathException("출발역과 도착역이 연결되어 있지 않습니다.");
    }
    return createPath((int) graphPath.getWeight(), graphPath.getVertexList());
  }

  private Path createPath(int distance, List<Station> shortestPath) {
    return new Path(distance, shortestPath);
  }

  private DijkstraShortestPath<Station, Long> createDijkstraShortestPath(
      List<Sections> sectionsList, Station source) {
    graph.addVertex(source);
    sectionsList.stream()
        .flatMap(section -> section.getSortedStations().stream())
        .collect(Collectors.toSet())
        .forEach(graph::addVertex);
    sectionsList.stream()
        .flatMap(section -> section.getSortedSection().stream())
        .forEach(section -> graph
            .setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()));
    return new DijkstraShortestPath(graph);
  }

  private void validate(Station source, Station target) {
    if (sectionsList.size() < MIN) {
      throw new InvalidStationPathException("시작역이나 도착역이 노선에 등록되어 있지 않습니다.");
    }
    if (source.equals(target)) {
      throw new InvalidStationPathException("출발역과 도착역은 서로 달라야 합니다.");
    }
  }
}
