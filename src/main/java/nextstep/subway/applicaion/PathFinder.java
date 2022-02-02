package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SourceTargetEqualException;
import nextstep.subway.exception.StationNotExistsOnTheLineException;
import nextstep.subway.exception.StationsNotConnectedException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class PathFinder {

	public static ShortestPathResponse findShortestPath(List<Line> lines, Station sourceStation, Station targetStation) {
		validateSourceAndTargetStationOrElseThrow(lines, sourceStation, targetStation);
		Multigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

		for (Section section : Line.getAllSections(lines)) {
			graph.addVertex(section.getUpStation());
			graph.addVertex(section.getDownStation());
			graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), toDouble(section.getDistance()));
		}

		GraphPath<Station, DefaultWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(graph, sourceStation, targetStation);

		validateStationsAreConnectedOrElseThrows(shortestPath, sourceStation, targetStation);
		return new ShortestPathResponse(shortestPath.getVertexList(), toInteger(shortestPath.getWeight()));
	}

	private static void validateStationsAreConnectedOrElseThrows(GraphPath<Station, DefaultWeightedEdge> shortestPath,
																 Station sourceStation, Station targetStation) {
		if (Objects.isNull(shortestPath)) {
			throw new StationsNotConnectedException(sourceStation.getName(), targetStation.getName());
		}
	}

	private static void validateSourceAndTargetStationOrElseThrow(List<Line> lines, Station sourceStation, Station targetStation) {
		validateSourceAndTargetStationSame(sourceStation, targetStation);
		validateStationIsExistsOnLinesOrElseThrows(lines, sourceStation, targetStation);
	}

	private static void validateSourceAndTargetStationSame(Station sourceStation, Station targetStation) {
		if (sourceStation.equals(targetStation)) {
			throw new SourceTargetEqualException(sourceStation.getName());
		}
	}

	private static void validateStationIsExistsOnLinesOrElseThrows(List<Line> lines, Station sourceStation, Station targetStation) {
		Set<Station> stations = Line.getAllSections(lines)
				.stream()
				.map(section -> asList(section.getUpStation(), section.getDownStation()))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());

		if (!stations.contains(sourceStation)) {
			throw new StationNotExistsOnTheLineException(sourceStation.getName());
		}
		if (!stations.contains(targetStation)) {
			throw new StationNotExistsOnTheLineException(targetStation.getName());
		}
	}

	private static int toInteger(double weight) {
		return Double.valueOf(weight).intValue();
	}

	private static double toDouble(Distance distance) {
		return distance.getDistance();
	}
}
