package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
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
    private final GraphPath<Station, DefaultWeightedEdge> path;
    private final List<Station> shortestPath;

    public ShortestPath(List<Line> lines, Station sourceStation, Station targetStation) {
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

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        shortestPath = path == null ? Collections.emptyList() : path.getVertexList();
    }

    public List<Station> getPath() {
        return Collections.unmodifiableList(shortestPath);
    }

    public int getDistance() {
        return path == null ? 0 : (int) path.getWeight();
    }
}
