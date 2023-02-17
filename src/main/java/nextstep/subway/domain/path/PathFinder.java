package nextstep.subway.domain.path;

import nextstep.subway.applicaion.StationNotFoundException;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * template method pattern 적용
 */
public abstract class PathFinder {

    private final SubwayMap subwayMap;

    protected PathFinder(SubwayMap subwayMap) {
        this.subwayMap = subwayMap;
    }

    protected abstract GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station departureStation, Station destinationStation);

    public Path findPath(Station departureStation, Station destinationStation) {
        verifyDepartureAndDestinationNotEqual(departureStation, destinationStation);
        verifyStationExistence(departureStation, destinationStation);

        GraphPath<Station, DefaultWeightedEdge> path = getShortestPath(departureStation, destinationStation);

        verifyPathExistence(path);

        return new Path(path.getVertexList(), new Distance((int) path.getWeight()));
    }

    private static void verifyPathExistence(GraphPath<Station, DefaultWeightedEdge> result) {
        if (result == null) {
            throw new DepartureDestinationCannotReachableException();
        }
    }

    private static void verifyDepartureAndDestinationNotEqual(Station departureStation, Station destinationStation) {
        if (departureStation.equals(destinationStation)) {
            throw new DepartureDestinationCannotSameException(departureStation, destinationStation);
        }
    }

    private void verifyStationExistence(Station departureStation, Station destinationStation) {
        if (!subwayMap.hasStation(departureStation)) {
            throw new StationNotFoundException(departureStation.getId());
        }

        if (!subwayMap.hasStation(destinationStation)) {
            throw new StationNotFoundException(destinationStation.getId());
        }
    }
}
