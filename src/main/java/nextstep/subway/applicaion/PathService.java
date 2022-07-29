package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.applicaion.dto.StationDto;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationDijkstraShortestPath;
import nextstep.subway.domain.StationWeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {

    private final SectionService sectionService;
    private final StationService stationService;

    public PathResponse findShortPath(PathRequest pathRequest) {
        Station findSource = stationService.findById(pathRequest.getSource());
        Station findTarget = stationService.findById(pathRequest.getTarget());

        List<Section> sections = SectionDto.toEntity(sectionService.findAll());
        List<Station> stations = StationDto.toEntity(stationService.findAllStations());

        StationWeightedMultigraph graph = new StationWeightedMultigraph();
        graph.setGraph(stations, sections);
        StationDijkstraShortestPath stationShortPath = new StationDijkstraShortestPath(graph, findSource, findTarget);

        List<StationDto> stationDto = StationDto.from(stationShortPath.getVertexList());
        return new PathResponse(StationResponse.from(stationDto), stationShortPath.getWeight());
    }




}
