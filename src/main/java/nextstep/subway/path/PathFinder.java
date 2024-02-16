package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.station.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathFinder {

    public PathResponse shortestPath(Station sourceStation, Station targetStation, List<Line> lines){
        List<Section> sections = allSections(lines);
        Set<Station> stations = allStations(sections);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = makeWeightedMultigraph(stations, sections);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        List<Station> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();

        return new PathResponse(shortestPath, dijkstraShortestPath.getPathWeight(sourceStation, targetStation));
    }

    private List<Section> allSections(List<Line> lines) {
        List<Section> sections = new ArrayList<>();
        lines.forEach(line -> sections.addAll(line.getSections().get()));
        return sections;
    }

    private static Set<Station> allStations(List<Section> sections) {
        Set<Station> stations = new HashSet<>();
        sections.forEach(s -> stations.addAll(s.stations()));
        return stations;
    }

    private static WeightedMultigraph<Station, DefaultWeightedEdge> makeWeightedMultigraph(Set<Station> stations, List<Section> sections) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        stations.forEach(graph::addVertex);
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        return graph;
    }
}
