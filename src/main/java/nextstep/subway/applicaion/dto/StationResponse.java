package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StationResponse {
    private Long id;
    private String name;

    public static List<StationResponse> from(Line line) {
        if (line.isSectionsEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getStations();

        return StationResponse.from(stations);
    }

    private static List<StationResponse> from(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    private static StationResponse from(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
