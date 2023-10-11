package nextstep.subway.domain;

import nextstep.subway.common.exception.pathFinder.DisconnectedPathException;
import nextstep.subway.common.exception.pathFinder.SameStationException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathFinder {
    private Lines lines;
    private List<Section> sections;

    private Station source;
    private Station target;

    private List<Station> pathStations;
    private Integer distance;

    public PathFinder(List<Line> lines) {
        this.lines = Lines.from(lines);
        this.sections = this.lines.getSections();
    }

    public List<Station> getPathStations() {
        return this.pathStations;
    }

    public Integer getDistance() {
        return this.distance;
    }

    public void calculatePath(Station source, Station target) {
        if (source.equals(target)) {
            throw new SameStationException();
        }
        this.source = source;
        this.target = target;
        this.calculatePath();
    }

    private void calculatePath() {
        calculateStations();
        calculateDistance();
    }

    private void calculateStations() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
                DefaultWeightedEdge.class
        );
        sections.forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(
                    graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance()
            );

        });
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        try {
            this.pathStations = dijkstraShortestPath.getPath(this.source, this.target).getVertexList();
        } catch (IllegalArgumentException e) {
            throw new DisconnectedPathException();
        }

    }

    private void calculateDistance() {
        int distance = 0;
        for (int i = 0; i < pathStations.size() - 1; i++) {
            Station station = pathStations.get(i);
            Optional<Section> sect = sections.stream().filter(section -> section.getUpStation().equals(station)).findFirst();
            if (sect.isPresent()) {
                distance += sect.get().getDistance();
            }
        }
        this.distance = distance;
    }
}
