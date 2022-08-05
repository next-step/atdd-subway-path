package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
public class PathResponse {

    List<Station> paths;

    int distance;

    public PathResponse() {

    }

    public PathResponse(List<Station> paths, int distance) {
        this.paths = paths;
        this.distance = distance;
    }

}