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

    private DijkstraShortestPath dijkstraShortestPath;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    private Lines lines;

    public DijkstraStrategy(List<Line> lines) {
        this.lines = new Lines(lines);
        addVertex();
        addEdgeWith();
        dijkstraShortestPath = new DijkstraShortestPath(graph);

    }

    private void addEdgeWith() {
        lines.findAllSections().stream()
                .forEach(section ->
                        graph.setEdgeWeight(
                                graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void addVertex() {
        lines.findAllStations().stream().forEach(station -> graph.addVertex(station));
    }

    @Override
    public Stations getPath(Station source, Station target) {
        try {
            List<Station> stations = dijkstraShortestPath.getPath(source, target).getVertexList();
            List<GraphPath> paths = new KShortestPaths(graph, stations.size()).getPaths(source, target);
            double distance = paths.stream().mapToDouble(path -> path.getWeight()).sum();
            return new Stations(stations, distance);
        }catch(Exception e){
            e.printStackTrace();
        }

        throw new CannotFindPathException("출발역과 도착역이 연결되지 않았습니다.");
    }
}
