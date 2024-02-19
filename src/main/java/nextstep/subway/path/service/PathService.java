package nextstep.subway.path.service;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.presentation.response.FindPathResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.dto.ShowStationDto;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PathService {

    private LineRepository lineRepository;

    public PathService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public FindPathResponse findShortestPath(Long source, Long target) {

        List<Line> lines = lineRepository.findAll();

        List<Section> sections = lines.stream()
                .map(line -> line.getSections())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Station> stations = lines.stream()
                .map(line -> line.getStations())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        stations.stream().forEach(station -> graph.addVertex(station.getStationId()));
        sections.stream().forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation().getStationId(), section.getDownStation().getStationId()), section.getDistance()));

        GraphPath shortestPath = new DijkstraShortestPath(graph).getPath(source, target);
        List<Long> shortestPathStations = shortestPath.getVertexList();

        List<ShowStationDto> pathStations = shortestPathStations.stream()
                .map(stationId -> stations.stream()
                        .filter(station -> station.getStationId().equals(stationId))
                        .findFirst()
                        .map(ShowStationDto::from)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return FindPathResponse.of(pathStations, (int) shortestPath.getWeight());
    }

}
