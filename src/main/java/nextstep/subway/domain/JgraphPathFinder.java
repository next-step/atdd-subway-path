package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class JgraphPathFinder implements PathFinderStrategy {

  private final List<Section> sections;

  private JgraphPathFinder(List<Section> sections) {
    this.sections = sections;
  }

  public static JgraphPathFinder of(List<Section> sections) {
    return new JgraphPathFinder(sections);
  }

  private DirectedGraph drawGraph(List<Section> sections) {
    SimpleDirectedWeightedGraph<Station, SectionEdge> graph = new SimpleDirectedWeightedGraph<>(
        SectionEdge.class);

    sections.stream()
        .flatMap(section -> section.getStations().stream())
        .collect(Collectors.toSet())
        .forEach(station -> graph.addVertex(station));

    sections.forEach(section -> {
          SectionEdge edge = SectionEdge.of(section);
          graph.addEdge(section.getUpStation(), section.getDownStation(), edge);
          graph.setEdgeWeight(edge, section.getDistance());
        });

    return graph;
  }

  private void sourceTargetValidate(Station source, Station target) {
    if (isSameStation(source, target)) {
      throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
    }
  }

  private boolean isSameStation(Station source, Station target) {
    return source.equals(target);
  }

  @Override
  public Path findShortestPath(Station source, Station target) {
    sourceTargetValidate(source, target);

    DirectedGraph<String, SectionEdge> graph = drawGraph(sections);

    ShortestPathAlgorithm<Station, SectionEdge> dijkstraShortestPath =
        new DijkstraShortestPath(graph);

    GraphPath<Station, SectionEdge> path = dijkstraShortestPath.getPath(source, target);

    if (path == null) {
      throw new IllegalArgumentException("연결되어 있지 않은 구간입니다.");
    }

    List<Section> sections = path.getEdgeList()
        .stream()
        .map(SectionEdge::getSection)
        .collect(Collectors.toList());

    return Path.of(Sections.of(sections), (long) path.getWeight());
  }

}
