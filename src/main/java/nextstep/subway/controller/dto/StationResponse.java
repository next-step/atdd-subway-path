package nextstep.subway.controller.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    @Builder
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse stationToStationResponse(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }

    public static List<StationResponse> sectionsToStationResponses(Line line) {
        List<Station> orderedStations = line.getSections().getOrderedStations();

        return orderedStations.stream()
                .map(it -> stationToStationResponse(it))
                .collect(Collectors.toList());

    }

    public static List<StationResponse> stationsToStationResponses(List<Station> stations) {
        return stations.stream()
                .map(it -> stationToStationResponse(it))
                .collect(Collectors.toList());

    }
}
