package nextstep.subway.path.domain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {

  public StationPath findPath(List<Sections> sectionsList,Station source, Station target) {
    WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    graph.addVertex(source);
    //vertex 추가
    sectionsList.stream()
        .flatMap(section -> section.getSortedStations().stream())
        .collect(Collectors.toSet())
        .forEach(graph::addVertex);
    //edge 추가
    sectionsList.stream()
        .flatMap(section -> section.getSortedSection().stream())
        .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(),section.getDownStation()),section.getDistance()));
    DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
    GraphPath graphPath = dijkstraShortestPath.getPath(source,target);
    List<Station> shortestPath = graphPath.getVertexList();
    int distance = (int) graphPath.getWeight();
    return new StationPath(distance,shortestPath);
  }
}
