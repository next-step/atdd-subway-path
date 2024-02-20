package nextstep.subway.applicaion;

import java.util.Collection;
import java.util.Optional;
import nextstep.subway.applicaion.vo.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class PathFinder {

  public Optional<Path> find(Collection<Section> sections, Station source, Station target) {
    verifyRequiredArguments(sections, source, target);

    final var graph = buildGraph(sections);
    final var path = new DijkstraShortestPath<>(graph).getPath(source, target);

    return Optional.ofNullable(path)
        .map(it -> Path.from(it.getVertexList(), Double.valueOf(it.getWeight()).intValue()));
  }

  private void verifyRequiredArguments(Collection<Section> sections, Station source, Station target) {
    if (sections == null) {
      throw new IllegalArgumentException("구간 정보가 없습니다.");
    }

    if (source == null) {
      throw new IllegalArgumentException("출발역 정보가 없습니다.");
    }

    if (target == null) {
      throw new IllegalArgumentException("도착역 정보가 없습니다.");
    }
  }

  // TODO Graph DB or Cache
  private WeightedMultigraph<Station, DefaultWeightedEdge> buildGraph(
      final Collection<Section> sections
  ) {
    final var graph = WeightedMultigraph.<Station, DefaultWeightedEdge>builder(DefaultWeightedEdge.class).build();

    sections.forEach(section -> {
      // add station ID
      graph.addVertex(section.getUpStation());
      graph.addVertex(section.getDownStation());

      // add edge
      final var edge = graph.addEdge(section.getUpStation(), section.getDownStation());
      graph.setEdgeWeight(edge, section.getDistance());
    });

    return graph;
  }
}
