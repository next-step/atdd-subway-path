package atdd.station.application;

import atdd.station.application.dto.ShortestPathResponseDto;
import atdd.station.application.dto.StationResponseDto;
import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayMap;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubwayMapQueryService {
    private SubwayLineQueryService subwayLineQueryService;
    private SubwaySectionQueryService subwaySectionQueryService;

    public SubwayMapQueryService(SubwayLineQueryService subwayLineQueryService,
                                 SubwaySectionQueryService subwaySectionQueryService) {

        this.subwayLineQueryService = subwayLineQueryService;
        this.subwaySectionQueryService = subwaySectionQueryService;
    }

    @Cacheable(value = "shortestPathCache", key = "{#startStationId, #destinationStationId}")
    public ShortestPathResponseDto getShortestPath(Long startStationId,
                                                   Long destinationStationId) {

        List<SubwayLine> subwayLines = subwayLineQueryService.findSubwayLines();

        SubwayMap subwayMap = new SubwayMap(subwayLines);
        List<Station> shortestPathStations = subwayMap.getShortestPath(startStationId, destinationStationId);

        List<StationResponseDto> stationResponseDtos = shortestPathStations.stream()
                .map(station -> StationResponseDto.of(station, subwaySectionQueryService.getSubwayLines(station.getId())))
                .collect(Collectors.toList());

        return ShortestPathResponseDto.of(stationResponseDtos);
    }

}
