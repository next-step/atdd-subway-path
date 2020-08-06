package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.line.dto.LineStationResponses;
import nextstep.subway.map.dto.PathResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.map.dto.SearchType;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.exception.StationSameExcepetion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public PathResponse findPath(Long source, Long target, SearchType type) {
        checkStations(source, target);

        List<LineResponse> lineResponses = lineService.findAllLineAndStations();

        PathResult pathResult = graph.findPath(lineResponses, source, target, type);

        List<StationResponse> stationResponses = pathResult.getStationIds().stream()
                .map(this::getStationResponse)
                .collect(Collectors.toList());

        LineStationResponses lineStationResponses = pathResult.getLineStationResponse(
                extractLineStationResponse(lineResponses));

        return PathResponse.of(stationResponses, lineStationResponses);
    }

    private LineStationResponses extractLineStationResponse(List<LineResponse> lineResponses) {
        return new LineStationResponses(lineResponses.stream()
                        .flatMap(it -> it.getStations().stream())
                        .collect(Collectors.toList()));
    }

    private StationResponse getStationResponse(Long stationId) {
        Optional<Station> findStation = stationRepository.findById(stationId);
        if (findStation.isPresent()) {
            return StationResponse.of(findStation.get());
        }
        return new StationResponse();
    }

    private void checkStations(Long source, Long target) {
        if (source == null || target == null) {
            throw new StationNotFoundException("역이 존재하지않습니다.");
        }

        if (source == target) {
            throw new StationSameExcepetion("동일한 역을 조회하여 에러 발생");
        }

        if (!stationRepository.existsById(source) || !stationRepository.existsById(target)) {
            throw new StationNotFoundException();
        }
    }

}
