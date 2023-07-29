package nextstep.subway.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import nextstep.subway.exception.PathNotConnectedStationException;
import nextstep.subway.exception.PathSameStationException;
import nextstep.subway.exception.SectionNotIncludedException;

public class PathFinder {

	private final DijkstraShortestPath<Station, SectionEdge> dijkstraShortestPath;
	private final List<Line> lines;

	public PathFinder(List<Line> lines) {
		this.lines = lines;
		this.dijkstraShortestPath = new DijkstraShortestPath<>(generateGraph(lines));
	}

	private WeightedMultigraph<Station, SectionEdge> generateGraph(List<Line> lines) {
		WeightedMultigraph<Station, SectionEdge> graph = new WeightedMultigraph<>(SectionEdge.class);

		lines.forEach(line -> {
			line.getStations()
				.forEach(graph::addVertex);
			line.getSections()
				.forEach(section -> {
					SectionEdge sectionEdge = new SectionEdge(section);
					graph.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
					graph.setEdgeWeight(sectionEdge, section.getDistance());
				});
		});

		return graph;
	}

	public Path findPath(Station source, Station target) {
		validateStation(source, target);
		GraphPath<Station, SectionEdge> graphPath = dijkstraShortestPath.getPath(source, target);
		validateGraphPath(graphPath);
		return new Path(graphPath.getEdgeList()
			.stream()
			.map(SectionEdge::getSection)
			.collect(Collectors.toUnmodifiableList()));
	}

	private static void validateGraphPath(GraphPath<Station, SectionEdge> graphPath) {
		if (graphPath == null) {
			throw new PathNotConnectedStationException();
		}
	}

	private void validateStation(Station source, Station target) {
		if (source.equalsId(target.getId())) {
			throw new PathSameStationException();
		}

		Set<Station> stationSet = lines.stream()
			.flatMap(line -> line.getStations().stream())
			.collect(Collectors.toSet());

		if (!(stationSet.contains(source) && stationSet.contains(target))) {
			throw new SectionNotIncludedException();
		}
	}
}
