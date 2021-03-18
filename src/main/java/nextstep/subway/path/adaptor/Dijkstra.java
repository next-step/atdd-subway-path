package nextstep.subway.path.adaptor;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.application.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathStationResponse;
import nextstep.subway.path.exception.NotFoundStationPath;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class Dijkstra implements PathFinder {

    private WeightedMultigraph< Station, DefaultWeightedEdge > graph;

    public Dijkstra(){
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    }

    @Override
    public PathResponse searchShortestPath(Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        final Optional optionalPath = Optional.ofNullable(dijkstraShortestPath.getPath(source, target));
        if (!optionalPath.isPresent()) {
            throw new NotFoundStationPath(MessageFormat.format("{0} 부터 {1} 까지 경로를 찾지 못했습니다.", source.getName(), target.getName()));
        }
        final GraphPath stationPath = (GraphPath) optionalPath.get();
        int weight = (int)stationPath.getWeight();
        return new PathResponse(createStationResponse(stationPath.getVertexList()), weight);
    }

    @Override
    public void initialize(List< Line > lines) {
        lines.stream().forEach(line -> loadStation(line.getStations()));
        lines.stream().forEach(line -> loadSection(line.getSections()));
    }

    private void loadSection(List< Section > sections) {
        sections.stream()
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void loadStation(List<Station> stations) {
        stations.stream().forEach(station -> graph.addVertex(station));
    }

    public List< PathStationResponse > createStationResponse(List<Station> path) {
        return path.stream()
                .map(it -> PathStationResponse.of(it))
                .collect(Collectors.toList());
    }
}
