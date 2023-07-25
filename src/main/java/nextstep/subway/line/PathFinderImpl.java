package nextstep.subway.line;

import java.util.List;
import nextstep.subway.station.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class PathFinderImpl implements PathFinder {

    private static WeightedMultigraph<Station, DefaultWeightedEdge> putAllStationToWeightedMultiGraph(
            List<Line> allStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allStation.stream()
                .map(Line::getPathInfo)
                .flatMap(List::stream)
                .forEach(pathInfo -> putInfo(graph, pathInfo));
        return graph;
    }

    private static void putInfo(WeightedMultigraph<Station, DefaultWeightedEdge> graph, PathInfo pathInfo) {
        Station upStation = pathInfo.getUpStation();
        Station downStation = pathInfo.getDownStation();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation, downStation), pathInfo.getDistance());
    }

    public PathResponse findShortestDistance(Station sourceStation, Station targetStation, List<Line> allLine) {
        var dijkstraShortestPath = new DijkstraShortestPath<>(putAllStationToWeightedMultiGraph(allLine));
        var shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (shortestPath == null) {
            throw new UnreachableDestinationException();
        }
        List<Station> stations = shortestPath.getVertexList();
        double distance = shortestPath.getWeight();
        return new PathResponse(stations, (long) distance);
    }

}
