package subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.*;
import subway.domain.service.ShortestStationPath;
import subway.domain.service.StationShortestPathCalculateService;
import subway.exception.StationLineSearchFailException;
import subway.service.dto.StationPathResponse;
import subway.service.dto.StationResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationPathService {
    private final StationLineRepository stationLineRepository;
    private final StationRepository stationRepository;
    private final StationShortestPathCalculateService stationShortestPathCalculateService;

    @Transactional(readOnly = true)
    public StationPathResponse searchStationPath(Long startStationId, Long destinationStationId) {
        checkValidPathFindRequest(startStationId, destinationStationId);

        final List<Station> totalStations = stationRepository.findAll();
        final List<StationLineSection> totalStationLineSections = stationLineRepository.findAll()
                .stream()
                .map(StationLine::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        final Station startStation = stationRepository.findById(startStationId)
                .orElseThrow(() -> new StationLineSearchFailException("there is no start station"));
        final Station destinationStation = stationRepository.findById(destinationStationId)
                .orElseThrow(() -> new StationLineSearchFailException("there is no destination station"));

        final ShortestStationPath path = stationShortestPathCalculateService.calculateShortestPath(startStation, destinationStation, totalStationLineSections);

        final Map<Long, Station> stationMap = totalStations.stream()
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        final List<Long> pathStationIds = path.getStationIds();

        return StationPathResponse.builder()
                .stations(pathStationIds.stream()
                        .map(stationMap::get)
                        .map(StationResponse::fromEntity)
                        .collect(Collectors.toList()))
                .distance(path.getDistance())
                .build();
    }

    private void checkValidPathFindRequest(Long startStationId, Long destinationStationId) {
        if (startStationId.equals(destinationStationId)) {
            throw new StationLineSearchFailException("start station and destination station are equals");
        }
    }
}
