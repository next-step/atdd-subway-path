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


  public List<Station> getPath(Station sourceStation, Station endStation) {
    validateStationEmpty(sourceStation);
    validateStationEmpty(endStation);
    validateDuplicateStation(sourceStation, endStation);
    return dijkstraShortestPath.getPath(sourceStation, endStation).getVertexList();
  }

  public int getDistance(Station sourceStation, Station endStation) {
    validateStationEmpty(sourceStation);
    validateStationEmpty(endStation);
    validateDuplicateStation(sourceStation, endStation);
    return (int) dijkstraShortestPath.getPathWeight(sourceStation, endStation);
  }

  private void validateStationEmpty(Station station) {
    if (!graph.containsVertex(station)) {
      throw new CustomException(PathErrorMessage.PATH_STATION_EMPTY);
    }
  }

  private void validateDuplicateStation(Station sourceStation, Station endStation) {
    if (sourceStation.equals(endStation)) {
      throw new CustomException(PathErrorMessage.STATION_DUPLICATE);
    }
  }
}
