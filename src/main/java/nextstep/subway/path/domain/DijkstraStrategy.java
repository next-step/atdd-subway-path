package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class DijkstraStrategy implements PathStrategy{

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    private DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
    private static int FASTEST_INDEX = 0;

    @Override
    public Stations getPath(Station source, Station target, Lines lines) {

        addVertex(lines);
        addEdgeWith(lines);

        try {
            List<Station> stations = dijkstraShortestPath.getPath(source, target).getVertexList();
            List<GraphPath> paths = new KShortestPaths(graph, stations.size()).getPaths(source, target);
            double distance = paths.get(FASTEST_INDEX).getWeight();

            return new Stations(stations, distance);
        }catch(IllegalArgumentException e){
            throw new CannotFindPathException("연결되지 않았습니다.");
        }
    }

    private void addEdgeWith(Lines lines) {
        lines.findAllSections().stream()
                .forEach(section ->
                        graph.setEdgeWeight(
                                graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void addVertex(Lines lines) {
        lines.findAllStations().stream().forEach(station -> graph.addVertex(station));
    }
}
