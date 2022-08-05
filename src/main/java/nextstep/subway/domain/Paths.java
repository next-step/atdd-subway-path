package nextstep.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.exception.NoPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class Paths {

    private DijkstraShortestPath shortestPath;
    private Lines lines;

    private Paths(WeightedMultigraph<Station, DefaultWeightedEdge> paths, Lines lines) {
        this.shortestPath = new DijkstraShortestPath(paths);
        this.lines = lines;
    }

    public static Paths from(Lines lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> paths = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Station station : lines.getStations()) {
            paths.addVertex(station);
        }
        for (Section section : lines.getSections()) {
            paths.setEdgeWeight(
                    paths.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance()
            );
        }
        return new Paths(paths, lines);
    }

    public Path getShortestPath(Long source, Long target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException();
        }
        Station sourceStation = lines.getStation(source);
        Station targetStation = lines.getStation(target);

        return Optional.ofNullable(shortestPath.getPath(sourceStation, targetStation))
                .map(Path::new)
                .orElseThrow(NoPathException::new);
    }

}
