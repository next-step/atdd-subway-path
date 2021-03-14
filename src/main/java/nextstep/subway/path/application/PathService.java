package nextstep.subway.path.application;

import nextstep.subway.exceptions.InvalidPathPointException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(StationRepository stationRepository, LineRepository lineRepository, StationService stationService) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse searchShortestPath(Long source, Long target) {
        checkEqualsStation(source, target);

        GraphPath resultPath = PathFinder.condition()
                .edgeList(lineRepository.findAll())
                .vertexList(stationRepository.findAll())
                .startStation(stationService.findById(source))
                .endStation(stationService.findById(target))
                .search();


        List<Station> resultStations = resultPath.getVertexList();

        List<StationResponse> stationResponses = resultStations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, resultPath.getWeight());
    }

    private void checkEqualsStation(Long source, Long target) {
        if (source.equals(target)) {
            throw new InvalidPathPointException();
        }
    }
}
