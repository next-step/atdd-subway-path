package nextstep.subway.path.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final StationService stationService;
    private final SectionService sectionService;

    public PathService(StationService stationService,
            SectionService sectionService) {
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public PathResponse searchPath(Long source, Long target) {
        if (Objects.equals(source, target)) {
            throw new CustomException(ErrorCode.INVALID_PARAM);
        }

        Station sourceStation = stationService.getStation(source);
        Station targetStation = stationService.getStation(target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraPath = getGraph();
        GraphPath<Station, DefaultWeightedEdge> sourceTargetPath = dijkstraPath.getPath(sourceStation, targetStation);

        if (sourceTargetPath == null) {
            throw new CustomException(ErrorCode.STATIONS_ARE_NOT_CONNECTED);
        }

        List<Station> shortestPath = sourceTargetPath.getVertexList();
        Double distance = sourceTargetPath.getWeight();

        return new PathResponse(getStationResponse(shortestPath), distance);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> getGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        List<Station> stationList = stationService.getAllStations();
        List<Section> sectionList = sectionService.getAllSections();
        stationList.forEach(graph::addVertex);
        sectionList.forEach(section -> {
            DefaultWeightedEdge edge = graph.addEdge(section.getUpwardStation(), section.getDownwardStation());
            graph.setEdgeWeight(edge, section.getDistance());
        });
        return new DijkstraShortestPath<>(graph);
    }

    private List<StationResponse> getStationResponse(List<Station> stations) {
        return stations
            .stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }
}
