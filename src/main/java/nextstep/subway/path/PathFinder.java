package nextstep.subway.path;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.PathResult;
import nextstep.subway.path.domain.graph.Graph;
import nextstep.subway.path.domain.graph.Path;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PathFinder {

    private final Graph graph;

    public PathFinder(Graph graph) {
        this.graph = graph;
    }

    public PathResult findPath(List<Section> sections, Station source, Station target) {
        List<Station> stations = getStations(sections);
        Path path = graph.getPath(stations, sections);
        return path.find(source, target);
    }

    private List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
