package nextstep.subway.path.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.path.domain.PathGraph;
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

        PathGraph graph = getGraph();

        List<Station> shortestPath = graph.getShortestPath(sourceStation, targetStation);
        Double shortestDistance = graph.getDistance(sourceStation, targetStation);

        return new PathResponse(getStationResponse(shortestPath), shortestDistance);
    }

    private PathGraph getGraph() {
        List<Station> stationList = stationService.getAllStations();
        List<Section> sectionList = sectionService.getAllSections();
        return new PathGraph(stationList, sectionList);
    }

    private List<StationResponse> getStationResponse(List<Station> stations) {
        return stations
            .stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }
}
