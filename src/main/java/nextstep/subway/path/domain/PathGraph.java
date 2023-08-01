package nextstep.subway.path.domain;

import java.util.List;
import java.util.Objects;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathGraph {
  private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
  private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

  public PathGraph(List<Section> edges) {
    edges.forEach(section -> {
      graph.addVertex(section.getUpwardStation());
      graph.addVertex(section.getDownwardStation());
      DefaultWeightedEdge edge = graph.addEdge(section.getUpwardStation(), section.getDownwardStation());
      graph.setEdgeWeight(edge, section.getDistance());
    });
    dijkstraShortestPath = new DijkstraShortestPath<>(graph);
  }

  public List<Station> getShortestPath(Station source, Station target) {
    GraphPath<Station, DefaultWeightedEdge> sourceTargetPath = getSourcePath(source, target);
    return sourceTargetPath.getVertexList();
  }

  public Double getDistance(Station source, Station target) {
    GraphPath<Station, DefaultWeightedEdge> sourceTargetPath = getSourcePath(source, target);
    return sourceTargetPath.getWeight();
  }

  private GraphPath<Station, DefaultWeightedEdge> getSourcePath(Station source, Station target) {
    try {
      checkEqualStation(source, target);
      GraphPath<Station, DefaultWeightedEdge> sourceTargetPath = dijkstraShortestPath.getPath(source, target);
      if (sourceTargetPath == null) {
        throw new CustomException(ErrorCode.STATIONS_ARE_NOT_CONNECTED);
      }
      return sourceTargetPath;
    } catch (IllegalArgumentException e) {
      throw new CustomException(ErrorCode.STATIONS_ARE_NOT_CONNECTED);
    }
  }

  private void checkEqualStation(Station source, Station target) {
    if (Objects.equals(source, target)) {
      throw new CustomException(ErrorCode.INVALID_PARAM);
    }
  }
}
