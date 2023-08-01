package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class JgraphPathFinder implements PathFinderStrategy {

  private final List<Line> lines;

  private JgraphPathFinder(List<Line> lines) {
    this.lines = lines;
  }

  //TODO: PathFinder 를 DI 할지 아니면 STATIC FACTORY로 불변객체로 가져갈지
  //      도메인 로직을 service layer에서 최대한 내리고 싶어서
  //      이렇게 불변 객체로 진행했습니다.
  public static JgraphPathFinder of(List<Line> lines) {
    return new JgraphPathFinder(lines);
  }

  public DirectedGraph drawGraph(List<Line> lines) {
    SimpleDirectedWeightedGraph<Station, SectionEdge> graph = new SimpleDirectedWeightedGraph<>(
        SectionEdge.class);

    lines.stream()
        .flatMap(line -> line.getStations().stream())
        .collect(Collectors.toSet())
        .forEach(station -> graph.addVertex(station));

    lines.stream()
        .flatMap(line -> line.getSections().stream())
        .collect(Collectors.toList())
        .forEach(section -> {
          SectionEdge edge = SectionEdge.of(section);
          graph.addEdge(section.getUpStation(), section.getDownStation(), edge);
          graph.setEdgeWeight(edge, section.getDistance());
        });

    return graph;
  }

  @Override
  public void sourceTargetValidate(Station source, Station target) {
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

    DirectedGraph<String, SectionEdge> graph = drawGraph(lines);

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
