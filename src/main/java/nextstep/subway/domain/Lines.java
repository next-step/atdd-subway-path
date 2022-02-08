package nextstep.subway.domain;

import lombok.val;
import nextstep.subway.domain.object.Distance;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;

public class Lines {

    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public Distance getShortestPathDistance(Station sourceStation, Station targetStation) {
        val graph = makeGraph(this.lines);
        return getEdgeList(graph, sourceStation, targetStation)
                .stream()
                .map(Section::getDistance)
                .reduce(Distance::plus)
                .orElse(null);
    }

    private WeightedMultigraph<Station, Section> makeGraph(List<Line> lines) {
        val graph = new WeightedMultigraph<Station, Section>(Section.class);
        for (val line : lines) {
            for (val section : line.getSections()) {
                val upStation = section.getUpStation();
                val downStation = section.getDownStation();
                graph.addVertex(upStation);
                graph.addVertex(downStation);
                graph.addEdge(upStation, downStation, section);
            }
        }
        return graph;
    }

    private List<Section> getEdgeList(
            WeightedMultigraph<Station, Section> graph,
            Station sourceStation,
            Station targetStation
    ) {
        return new DijkstraShortestPath<>(graph)
                .getPath(sourceStation, targetStation)
                .getEdgeList();
    }

    public List<Station> getShortestPath(Station sourceStation, Station targetStation) {
        val graph = makeGraph(this.lines);
        validateStation(graph, sourceStation, targetStation);

        val shortestPath = getVertexList(graph, sourceStation, targetStation);
        return Collections.unmodifiableList(shortestPath);
    }

    private void validateStation(
            WeightedMultigraph<Station, Section> graph,
            Station source,
            Station target
    ) {
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException();
        }

        if (!graph.containsVertex(target)) {
            throw new IllegalArgumentException();
        }

        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }
    }

    private List<Station> getVertexList(
            WeightedMultigraph<Station, Section> graph,
            Station sourceStation,
            Station targetStation
    ) {
        return new DijkstraShortestPath<>(graph)
                .getPath(sourceStation, targetStation)
                .getVertexList();
    }
}
