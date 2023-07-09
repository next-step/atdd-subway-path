package nextstep.subway.applicaion.station.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.Station;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static nextstep.subway.applicaion.station.response.StationResponse toResponse(final Station station) {
        return new nextstep.subway.applicaion.station.response.StationResponse(station.getId(), station.getName());
    }

    public static List<nextstep.subway.applicaion.station.response.StationResponse> toResponses(final List<Station> stations) {
        return stations.stream()
                .map(nextstep.subway.applicaion.station.response.StationResponse::toResponse)
                .collect(Collectors.toUnmodifiableList());
    }
}
