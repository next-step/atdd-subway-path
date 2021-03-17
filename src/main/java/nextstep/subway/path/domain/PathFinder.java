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

  public StationPath findPath(List<Sections> sectionsList, Station source, Station target) {
    validate(source, target);
    DijkstraShortestPath dijkstraShortestPath = createDijkstraShortestPath(sectionsList,source,target);
    GraphPath graphPath = dijkstraShortestPath.getPath(source, target);
    if(graphPath == null) {
      throw new InvalidStationPathException("출발역과 도착역이 연결되어 있지 않습니다.");
    }
    return createStationPath((int) graphPath.getWeight(),graphPath.getVertexList());
  }

  private StationPath createStationPath(int distance, List<Station> shortestPath){
    return new StationPath(distance, shortestPath);
  }

  private DijkstraShortestPath createDijkstraShortestPath(List<Sections> sectionsList, Station source, Station target){
    WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(
        DefaultWeightedEdge.class);
    graph.addVertex(source);
    //vertex 추가
    sectionsList.stream()
        .flatMap(section -> section.getSortedStations().stream())
        .collect(Collectors.toSet())
        .forEach(graph::addVertex);
    //edge 추가
    sectionsList.stream()
        .flatMap(section -> section.getSortedSection().stream())
        .forEach(section -> graph
            .setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance()));
    return new DijkstraShortestPath(graph);
  }
  private void validate(Station source, Station target) {
    if (source.equals(target)) {
      throw new InvalidStationPathException("출발역과 도착역은 서로 달라야 합니다.");
    }
  }
}
