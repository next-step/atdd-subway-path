package nextstep.subway.path.application;

import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.PathRepository;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.SourceTargetNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class PathService {
    private final PathRepository pathRepository;
    private final StationRepository stationRepository;

    public PathService(PathRepository pathRepository, StationRepository stationRepository) {
        this.pathRepository = pathRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        Station sourceStation = findStationById(source);
        Station targetStation = findStationById(target);
        Path path = pathRepository.findShortestPath(sourceStation, targetStation);
        return PathResponse.of(path);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(SourceTargetNotFoundException::new);
    }
}
