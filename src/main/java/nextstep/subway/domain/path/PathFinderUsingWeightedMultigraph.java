package nextstep.subway.domain.path;

import static nextstep.subway.exception.CommonExceptionMessages.SAME_DEPARTURE_AND_DESTINAME;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.SectionEdge;
import nextstep.subway.domain.section.SectionMultigraph;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.InvalidPathSearchingException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;

public class PathFinderUsingWeightedMultigraph implements PathFinder {

    private SectionMultigraph<Station, SectionEdge> weightedMultigraph;

    public PathFinderUsingWeightedMultigraph(List<Line> lines) {
        makeGraph(lines);
    }

    public void makeGraph(List<Line> lines) {
        weightedMultigraph = new SectionMultigraph(SectionEdge.class);
        for (Line line : lines) {
            addVerticesAndEdgesOf(line);
        }
    }

    public void addVerticesAndEdgesOf(Line line) {
        Section section;
        for (int i = 0; i < line.getSections().size(); ++i) {
            section = line.getSections().get(i);
            weightedMultigraph.addVertex(section.getUpStation());
            weightedMultigraph.addVertex(section.getDownStation());
            weightedMultigraph.setEdge(
                    weightedMultigraph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance());
        }
    }

    public ShortestPath executeDijkstra(Station sourceStation, Station targetStation) {
        validateSourceTarget(sourceStation, targetStation);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath
                = new DijkstraShortestPath(weightedMultigraph);
        GraphPath<Station, DefaultWeightedEdge> path
                = dijkstraShortestPath.getPath(sourceStation, targetStation);

        return new ShortestPath(path);
    }

    public List<ShortestPath> executeKShortest(Station sourceStation, Station targetStation) {
        validateSourceTarget(sourceStation, targetStation);
        KShortestPaths<Station, DefaultWeightedEdge> kShortestPaths
                = new KShortestPaths(weightedMultigraph, 5);
        List<GraphPath<Station, DefaultWeightedEdge>> paths
                = kShortestPaths.getPaths(sourceStation, targetStation);

        return paths.stream()
                .map(ShortestPath::new)
                .collect(Collectors.toList());
    }

    public void validateSourceTarget(Station source, Station target) {
        if (source.equals(target)) {
            throw new InvalidPathSearchingException(SAME_DEPARTURE_AND_DESTINAME);
        }
    }
}
