package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Path;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class PathResponse {

    List<StationResponse> paths = new ArrayList<>();

    int distance;

    public PathResponse(Path path) {
        path.getStations().forEach(station -> this.paths.add(new StationResponse(station)));
        this.distance = path.getDistance();
    }

}