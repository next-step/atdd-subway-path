package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestPathException;
import nextstep.subway.repository.LineRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final StationService stationService;

    private final LineRepository lineRepository;

    public PathService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public PathResponse getPaths(Long source, Long target) {

        if (Objects.equals(source, target)) {
            throw new BadRequestPathException("출발역과 도착역이 동일합니다.");
        }

        Station startStation = stationService.getStations(source);
        Station endStation = stationService.getStations(target);
        Path shortPaths = getShortPaths(startStation, endStation);

        List<StationResponse> stationResponses = shortPaths.getStations().stream().map(StationResponse::new).toList();
        return new PathResponse(stationResponses, shortPaths.getDistance());
    }

    private Path getShortPaths(Station source, Station target) {
        return new PathFinder(lineRepository.findAll().stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())).getShortestPath(source, target);
    }
}
