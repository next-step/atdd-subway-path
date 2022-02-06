package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.ExploreResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.handler.exception.StationException;
import nextstep.subway.handler.validator.ExploreValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.handler.exception.ErrorCode.STATION_NOT_FOUND_BY_ID;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public ExploreResponse explore(Long source, Long target) {
        Station sourceStation = findStationById(source);
        Station targetStation = findStationById(target);
        ExploreValidator.validateStationsIsSame(sourceStation, targetStation);

        pathFinder = new PathFinder(findAllLines());

        List<Station> exploredStations = exploreByStations(sourceStation, targetStation);

        return ExploreResponse.from(exploredStations);
    }

    private List<Station> exploreByStations(Station sourceStation, Station targetStation) {
        List<String> exploredStationNames = pathFinder.explore(sourceStation.getName(), targetStation.getName());

        List<Station> exploredStations = exploredStationNames.stream()
                .map(stationName -> stationRepository.getByName(stationName))
                .collect(Collectors.toList());
        return exploredStations;
    }

    private List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationException(STATION_NOT_FOUND_BY_ID));
    }
}
