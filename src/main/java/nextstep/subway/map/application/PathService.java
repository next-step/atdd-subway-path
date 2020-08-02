package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.PathResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineService lineService;
    private final Graph graph;
    private final StationRepository stationRepository;

    public PathService(LineService lineService, Graph graph, StationRepository stationRepository) {
        this.lineService = lineService;
        this.graph = graph;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        checkStations(source, target);

        List<LineResponse> lineResponses = lineService.findAllLineAndStations();

        PathResult pathResult = graph.findPath(lineResponses, source, target);

        List<StationResponse> stationResponses = pathResult.getStationIds().stream()
                .map(stationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, (int) pathResult.getWeight(), 0);
    }

    private void checkStations(Long source, Long target) {
        if (source == null || target == null) {
            throw new RuntimeException();
        }

        if (source == target) {
            throw new RuntimeException();
        }

        stationRepository.findById(source).orElseThrow(RuntimeException::new);
        stationRepository.findById(target).orElseThrow(RuntimeException::new);
    }
}
