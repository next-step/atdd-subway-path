package nextstep.subway.path.dto;

import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private Integer distance;
    private Integer duration;


    public PathResponse(List<Long> shortestPath, List<LineStationResponse> lineStations) {
        List<StationResponse> shortestPathStations = extractStationResponses(shortestPath, lineStations);
        List<LineStationResponse> shortestPathLineStations = findShortestPathLineStations(shortestPath, lineStations);

        this.stations = shortestPathStations;
        this.distance = shortestPathLineStations.stream().mapToInt(LineStationResponse::getDistance).sum();
        this.duration = shortestPathLineStations.stream().mapToInt(LineStationResponse::getDuration).sum();
    }

    private List<StationResponse> extractStationResponses(List<Long> shortestPath, List<LineStationResponse> lineStations) {
        Map<Long, StationResponse> stations = lineStations.stream()
                .map(LineStationResponse::getStation)
                .distinct()
                .collect(Collectors.toMap(StationResponse::getId, Function.identity()));

        return shortestPath.stream()
                .map(stations::get)
                .collect(Collectors.toList());
    }

    private List<LineStationResponse> findShortestPathLineStations(List<Long> shortestPath, List<LineStationResponse> lineStations) {
        List<LineStationResponse> shortestPathLineStations = new ArrayList<>();
        for (int i = 1; i < shortestPath.size(); i++) {
            Long stationId = shortestPath.get(i);
            Long preStationId = shortestPath.get(i - 1);

            LineStationResponse lineStation = lineStations.stream()
                    .filter(station -> Objects.equals(station.getStationId(), stationId))
                    .filter(station -> Objects.equals(preStationId, station.getPreStationId()))
                    .findAny()
                    .orElseThrow(RuntimeException::new);

            shortestPathLineStations.add(lineStation);
        }
        return shortestPathLineStations;
    }

    public PathResponse() {
    }


    public static PathResponse of(List<Long> shortestPath, List<LineStationResponse> lineStations) {
        return new PathResponse(shortestPath, lineStations);
    }


    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
