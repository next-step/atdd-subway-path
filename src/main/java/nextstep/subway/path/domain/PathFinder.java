package nextstep.subway.path.domain;


import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {

    private static GraphPath search(Condition condition) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = getGraphAfterDataEntry(condition);
        return searchShortestPath(graph, condition);
    }

    private static GraphPath searchShortestPath(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Condition condition) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath<>(graph);

        return dijkstraShortestPath.getPath(condition.startStation, condition.endStation);
    }

    private static WeightedMultigraph<Station, DefaultWeightedEdge> getGraphAfterDataEntry(Condition condition) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        condition.vertexList
                .forEach(graph::addVertex);

        condition.edgeList
                .forEach(line -> setAllEdgeWeightFromSections(graph, line));

        return graph;
    }

    private static void setAllEdgeWeightFromSections(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Line line) {
        Sections sections = line.getSections();
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    public static Condition condition() {
        return new Condition();
    }

    public static class Condition {
        private Station startStation;
        private Station endStation;
        private List<Station> vertexList;
        private List<Line> edgeList;

        public Condition startStation(Station val) {
            this.startStation = val;
            return this;
        }

        public Condition endStation(Station val) {
            this.endStation = val;
            return this;
        }

        public Condition vertexList(List<Station> val) {
            this.vertexList = val;
            return this;
        }

        public Condition edgeList(List<Line> val) {
            this.edgeList = val;
            return this;
        }

        public GraphPath search() {
            return PathFinder.search(this);
        }

    }
}
