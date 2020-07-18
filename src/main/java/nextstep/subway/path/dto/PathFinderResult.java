package nextstep.subway.path.dto;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinderResult {

    private final List<Long> stationIds;
    private final double weight;

    public PathFinderResult(List<Long> stationIds, double weight) {
        this.stationIds = stationIds;
        this.weight = weight;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public double getWeight() {
        return weight;
    }

    public PathResponse toPathResponse(List<LineResponse> lines) {
        final Map<Long, StationResponse> stationResponseMap = extractStationResponseMap(lines);
        final List<StationResponse> list = stationIds.stream().map(stationResponseMap::get).collect(Collectors.toList());
        final int distance = getDistance(lines, list);
        final int duration = getDuration(lines, list);
        return new PathResponse(list, distance, duration);
    }

    private int getDuration(List<LineResponse> lines, List<StationResponse> list) {
        return getLineStationResponseStream(lines, list).mapToInt(LineStationResponse::getDuration).sum();
    }

    private int getDistance(List<LineResponse> lines, List<StationResponse> list) {
        return getLineStationResponseStream(lines, list).mapToInt(LineStationResponse::getDistance).sum();
    }

    private Stream<LineStationResponse> getLineStationResponseStream(List<LineResponse> lines, List<StationResponse> list) {
        return lines.stream()
                .flatMap(lineResponse -> lineResponse.getStations().stream())
                .filter(lineStationResponse -> lineStationResponse.getStation().getId() == null)
                .filter(lineStationResponse -> list.contains(lineStationResponse.getStation().getId()));
    }

    private Map<Long, StationResponse> extractStationResponseMap(List<LineResponse> lines) {
        return lines.stream()
                .flatMap(lineResponse -> lineResponse.getStations().stream())
                .map(LineStationResponse::getStation)
                .distinct()
                .collect(Collectors.toMap(StationResponse::getId, stationResponse -> stationResponse));
    }
}
