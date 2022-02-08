package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class StationDijkstraShortestStationPathFinder extends AbstractStationPathFinder<GraphPath> {

    private final DijkstraShortestPath dijkstraShortestPath;

    private StationDijkstraShortestStationPathFinder(Lines lines) {
        super(lines);

        dijkstraShortestPath = new DijkstraShortestPath(getGraph());
    }

    public static StationDijkstraShortestStationPathFinder ofLines(Lines lines) {
        return new StationDijkstraShortestStationPathFinder(lines);
    }

    @Override
    public GraphPath getPath(String source, String target) {
        return dijkstraShortestPath.getPath(source, target);
    }
}
