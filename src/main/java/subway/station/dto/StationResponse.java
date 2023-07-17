package subway.station.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.station.model.Station;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationResponse {

    private Long id;

    private String name;

    public static StationResponse from(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }

    public static List<StationResponse> from(List<Station> stations) {
        return stations.stream()
                .map(station -> StationResponse.builder()
                        .id(station.getId())
                        .name(station.getName())
                        .build())
                .collect(Collectors.toList());

    }
}
