package nextstep.subway.path.service;

import java.util.List;
import nextstep.subway.path.controller.dto.PathResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;
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

    private WeightedMultigraph<Station, DefaultWeightedEdge> getGraph() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        List<Station> stationList = stationService.getAllStations();
        stationList.forEach(graph::addVertex);
        return graph;
    }


    public PathResponse searchPath(Long source, Long target) {

        return null;
    }
}
