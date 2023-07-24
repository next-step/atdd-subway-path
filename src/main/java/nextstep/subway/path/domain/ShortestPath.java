package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;

public class ShortestPath {
    private final GraphPath<Station, DefaultWeightedEdge> graphPath;
    private final List<Station> shortestPath;

    public ShortestPath(List<Line> lines, Station sourceStation, Station targetStation) {
        graphPath = createdGraphPath(lines, sourceStation, targetStation);
        validateGraphPath();

        shortestPath = graphPath.getVertexList();
    }

    private void validateGraphPath() {
        if (graphPath == null) {
            throw new PathNotFoundException();
        }
    }

    private GraphPath<Station, DefaultWeightedEdge> createdGraphPath(List<Line> lines, Station sourceStation, Station targetStation) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(createWeightedGraph(lines));
        return dijkstraShortestPath.getPath(sourceStation, targetStation);
    }

    private WeightedGraph<Station, DefaultWeightedEdge> createWeightedGraph(List<Line> lines) {
        WeightedGraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for (Line line : lines) {
            List<Section> sections = line.getSections();
            for (Section section : sections) {
                Station upStation = section.getUpStation();
                Station downStation = section.getDownStation();

                graph.addVertex(upStation);
                graph.addVertex(downStation);

                graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
            }
        }
        return graph;
    }

    public List<Station> getPath() {
        return Collections.unmodifiableList(shortestPath);
    }

    public int getDistance() {
        return (int) graphPath.getWeight();
    }
}
