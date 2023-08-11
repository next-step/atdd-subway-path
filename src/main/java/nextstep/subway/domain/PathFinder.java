package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.applicaion.dto.path.PathSearchResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.section.Section;
import nextstep.subway.exception.PathExceptionCode;
import nextstep.subway.exception.SubwayException;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

  private final List<Line> lines;
  private final Station startStation;
  private final Station targetStation;

  public PathFinder(List<Line> lines, Station startStation, Station targetStation) {
    this.lines = lines;
    this.startStation = startStation;
    this.targetStation = targetStation;
  }

  public PathSearchResponse findPath () {
    WeightedGraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    for (Line line : lines) {
      line.getStations().forEach(graph::addVertex);
      line.getSections().forEach(section -> addSectionToEdgeWithWeight(graph, section));
    }

    var dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    var path = dijkstraShortestPath.getPath(startStation, targetStation);

    if(path == null) {
      throw new SubwayException(PathExceptionCode.UNREACHABLE_PATH);
    }

    List<StationResponse> stations = path.getVertexList().stream()
        .map(StationResponse::new)
        .collect(Collectors.toUnmodifiableList());

    return new PathSearchResponse(stations, (int) path.getWeight());
  }

  private void addSectionToEdgeWithWeight(WeightedGraph<Station, DefaultWeightedEdge> graph, Section section) {
    graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
  }
}
