package nextstep.subway.path.domain;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private List<Sections> sections;

    public PathFinder(List<Sections> sections) {
        this.sections = sections;
        this.graph = createPathGraph();
    }

    public Path getShortestPath(Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath
                = new DijkstraShortestPath(graph);

        try {

            GraphPath<Station, Station> path = dijkstraShortestPath.getPath(source, target);
            return new Path(path.getVertexList(), path.getWeight());

        } catch (IllegalArgumentException ex) {
            throw new ApplicationException(ApplicationType.NOT_CONNECTED_STATION);
        }
    }

    private WeightedMultigraph createPathGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> createdGraph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for(Sections sec : this.sections) {
            sec.getSortedStations().forEach(createdGraph::addVertex);

            sec.getSections().forEach(section -> {
                createdGraph.setEdgeWeight(createdGraph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            });
        }

        return createdGraph;
    }
}
