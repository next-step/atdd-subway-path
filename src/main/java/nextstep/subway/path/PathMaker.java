package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathMaker {
    public static WeightedMultigraph<Station, DefaultWeightedEdge> makeGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for(Line line : lines) {
            List<Station> stations = line.getStations();
            addVertex(graph, stations);

            List<Section> sections = line.getSectionList();
            addEdge(graph, sections);
        }

        return graph;
    }

    private static void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Station> stations) {
        stations.stream()
                .filter(station -> !graph.containsVertex(station))
                .forEach(graph::addVertex);
    }

    private static void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.stream()
                .filter(section -> !graph.containsEdge(section.getUpStation(), section.getDownStation()))
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    public static PathResponse createPathResponse(GraphPath graphPath) {
        List<Station> shortestPathStations = graphPath.getVertexList();
        double shortestPathWeight = graphPath.getWeight();

        PathResponse pathResponses = PathResponse.createResponse(shortestPathStations, (int) shortestPathWeight);

        return pathResponses;
    }

}
