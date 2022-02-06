package nextstep.subway.domain;

import nextstep.subway.common.annotation.DomainService;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;

@DomainService
public class GraphService {

    public List<Station> getShortestPath(
            Station sourceStation,
            Station targetStation,
            List<Line> lines
    ) {
        var graph = makeGraph(lines);
        validateStation(graph, sourceStation, targetStation);

        try {
            var shortestPath = getShortestGraphPath(graph, sourceStation, targetStation).getVertexList();
            return Collections.unmodifiableList(shortestPath);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(sourceStation.getName() + " -> " + targetStation.getName() + " 경로가 존재하지 않습니다");
        }
    }

    public int getShortestPathDistance(
            Station sourceStation,
            Station targetStation,
            List<Line> lines
    ) {
        var graph = makeGraph(lines);
        return getShortestGraphPath(graph, sourceStation, targetStation)
                .getEdgeList().stream()
                .map(Section::getDistance)
                .reduce(0, Integer::sum);
    }

    private WeightedMultigraph<Station, Section> makeGraph(List<Line> lines) {
        var graph = new WeightedMultigraph<Station, Section>(Section.class);
        for (var line : lines) {
            for (var section : line.getSections().getOrderedSections()) {
                var upStation = section.getUpStation();
                var downStation = section.getDownStation();
                graph.addVertex(upStation);
                graph.addVertex(downStation);
                graph.addEdge(upStation, downStation, section);
            }
        }
        return graph;
    }

    private void validateStation(
            WeightedMultigraph<Station, Section> graph,
            Station source,
            Station target
    ) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("그래프에 존재하지 않는 역입니다. 역 id: " + source.getId());
        }

        if (!graph.containsVertex(target)) {
            throw new IllegalArgumentException("그래프에 존재하지 않는 역입니다. 역 id: " + target.getId());
        }

        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같은 역입니다. 역 id: " + source.getId());
        }
    }

    private GraphPath<Station, Section> getShortestGraphPath(
            WeightedMultigraph<Station, Section> graph,
            Station sourceStation,
            Station targetStation
    ) throws NullPointerException {
        return new DijkstraShortestPath<>(graph)
                .getPath(sourceStation, targetStation);
    }

}
