package nextstep.subway.domain.path;

import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

public class DijkstraPathFinder extends PathFinder {

    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public DijkstraPathFinder(SubwayMap subwayMap) {
        super(subwayMap);

        this.dijkstraShortestPath = new DijkstraShortestPath<>(subwayMap.getMap());
    }

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station departureStation, Station destinationStation) {
        return dijkstraShortestPath.getPath(departureStation, destinationStation);
    }
}
