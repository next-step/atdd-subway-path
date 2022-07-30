package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public static PathResponse from(Path path) {
        List<StationResponse> stations = new ArrayList<>();
        for (Station station : path.getStations()) {
            stations.add(StationResponse.from(station));
        }

        return PathResponse.builder()
                .stations(stations)
                .distance(path.getDistance().toInt())
                .build();
    }
}
