package nextstep.subway.domain;

import java.util.List;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.message.PathErrorMessage;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

  private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
  private DijkstraShortestPath dijkstraShortestPath;

  public PathFinder(List<Section> sections) {
    graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    for (Section section : sections) {
      graph.addVertex(section.getUpStation());
      graph.addVertex(section.getDownStation());

      graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    dijkstraShortestPath = new DijkstraShortestPath(graph);
  }


  public List<Station> getPath(Station sourceStation, Station targetStation) {
    validateStation(sourceStation, targetStation);
    return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
  }

  public int getDistance(Station sourceStation, Station targetStation) {
    validateStation(sourceStation, targetStation);
    return (int) dijkstraShortestPath.getPathWeight(sourceStation, targetStation);
  }

  private void validateStation(Station sourceStation, Station targetStation) {
    validateStationEmpty(sourceStation);
    validateStationEmpty(targetStation);
    validateDuplicateStation(sourceStation, targetStation);
  }

  private void validateStationEmpty(Station station) {
    if (!graph.containsVertex(station)) {
      throw new CustomException(PathErrorMessage.PATH_STATION_EMPTY);
    }
  }

  private void validateDuplicateStation(Station sourceStation, Station targetStation) {
    if (sourceStation.equals(targetStation)) {
      throw new CustomException(PathErrorMessage.STATION_DUPLICATE);
    }
  }
}
