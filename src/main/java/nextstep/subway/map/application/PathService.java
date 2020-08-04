package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.PathResponse;
import nextstep.subway.map.dto.PathResult;
import nextstep.subway.map.dto.SearchType;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.exception.StationSameExcepetion;
import org.springframework.stereotype.Service;

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

    public PathResponse findPath(Long source, Long target, SearchType type) {
        checkStations(source, target);

        List<LineResponse> lineResponses = lineService.findAllLineAndStations();

        PathResult pathResult = graph.findPath(lineResponses, source, target, type);

        List<StationResponse> stationResponses = pathResult.getStationIds().stream()
                .map(stationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(StationResponse::of)
                .collect(Collectors.toList());

        List<LineStationResponse> lineStationResponses = pathResult.getLineStationResponse(lineResponses.stream()
                .flatMap(it -> it.getStations().stream())
                .collect(Collectors.toList()));

        return PathResponse.of(stationResponses, getDistances(lineStationResponses), getDurations(lineStationResponses));
    }

    private int getDistances(List<LineStationResponse> lineStationResponses) {
        return lineStationResponses.stream().mapToInt(LineStationResponse::getDistance).sum();
    }

    private int getDurations(List<LineStationResponse> lineStationResponses) {
        return lineStationResponses.stream().mapToInt(LineStationResponse::getDuration).sum();
    }

    private void checkStations(Long source, Long target) {
        if (source == null || target == null) {
            throw new StationNotFoundException("역이 존재하지않습니다.");
        }

        if (source == target) {
            throw new StationSameExcepetion("동일한 역을 조회하여 에러 발생");
        }

        stationRepository.findById(source).orElseThrow(StationNotFoundException::new);
        stationRepository.findById(target).orElseThrow(StationNotFoundException::new);
    }

}
