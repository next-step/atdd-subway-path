package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathDto;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.applicaion.dto.StationDto;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {

    private final SectionService sectionService;
    private final StationService stationService;

    public PathDto findShortPath(PathRequest pathRequest) {
        Station findSource = stationService.findById(pathRequest.getSource());
        Station findTarget = stationService.findById(pathRequest.getTarget());

        List<Section> sections = SectionDto.toEntity(sectionService.findAll());
        List<Station> stations = StationDto.toEntity(stationService.findAllStations());

        Path path = new Path(stations, sections);
        return createPathDto(
                path.getShortestWithDijkstra(findSource, findTarget),
                path.getShortestDistance(findSource, findTarget)
        );
    }

    private PathDto createPathDto(List<Station> shortestPath, int shortestDistance) {
        return PathDto.of(shortestPath, shortestDistance);
    }


}
