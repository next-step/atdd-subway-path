package nextstep.subway.path.service;

import nextstep.subway.line.persistance.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.dto.PathsDto;
import nextstep.subway.path.domain.dto.StationDto;
import nextstep.subway.path.presentation.*;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.persistance.StationRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class PathService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathsResponse searchPath(long source, long target) {
        try {
            PathFinder pathFinder = new PathFinder(lineRepository.findAll());
            PathsDto pathsDto = pathFinder.findPath(getStation(source), getStation(target));
            return new PathsResponse(pathsDto.getDistance(),
                    pathsDto.getPaths()
                            .stream()
                            .map(it -> new StationDto(it.getId(), it.getName()))
                            .collect(Collectors.toList())
            );
        } catch (IllegalArgumentException e) {
            CannotFindPathException ex = new CannotFindPathException("경로 탐색이 불가합니다");
            ex.initCause(e);
            throw ex;
        }
    }

    private Station getStation(long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(Long.toString(stationId)));
    }

}
