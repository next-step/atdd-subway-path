package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.domain.StationGraphPath;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PathFinder {

    private final GraphService graphService;

    public PathFinder(GraphService graphService) {
        this.graphService = graphService;
    }

    public StationGraphPath getShortestPath(List<Section> sections, Station source, Station target){

        List<Station> stations = graphService.makeStations(sections);

        validateContains(stations, source, target);

        SubwayGraph graph = graphService.findGraph(sections,stations);

        return StationGraphPath.of(new DijkstraShortestPath<>(graph.getSubwayGraph()).getPath(source, target));
    }

    private void validateContains(List<Station> stations, Station source, Station target) {
        if (!stations.containsAll(Arrays.asList(source, target))) {
            throw new RuntimeException("구간 내 존재하지 않는 역입니다.");
        }
    }
}
