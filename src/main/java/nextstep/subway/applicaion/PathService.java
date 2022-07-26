package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(PathRequest pathRequest) {
        List<Line> findLines = lineRepository.findAll();
        Station sourceStation = findStationById(pathRequest.getSource());
        Station targetStation = findStationById(pathRequest.getTarget());

        PathFinder pathFinder = PathFinder.of(findLines);

        List<Station> findStations = pathFinder.findPath(sourceStation, targetStation);
        double pathWeight = pathFinder.findPathWeight(sourceStation, targetStation);

        return PathResponse.of(findStations, pathWeight);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
    }
}
