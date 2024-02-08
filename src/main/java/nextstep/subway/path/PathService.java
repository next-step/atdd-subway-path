package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.jgrapht.alg.util.Pair;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathService {
    private final PathFinder pathFinder;
    private final StationRepository stationRepository;

    public PathResponse showShortestPath(Long sourceId, Long targetId) {
        stationRepository.findById(sourceId).orElseThrow(EntityNotFoundException::new);
        stationRepository.findById(targetId).orElseThrow(EntityNotFoundException::new);

        Pair<List<String>, Integer> pair = pathFinder.findShortestPath(Long.toString(sourceId), Long.toString(targetId));
        List<StationResponse> stations = pair.getFirst().stream()
                .map(Long::parseLong)
                .map(stationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(StationResponse::from)
                .collect(Collectors.toList());

        int distance = pair.getSecond();

        return new PathResponse(stations, distance);
    }
}
