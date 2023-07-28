package nextstep.subway.dto;

import lombok.Getter;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
public class PathResponse {

    private List<Station> stations;
    private int distance;

    public static PathResponse createPathResponse(List<Station> path,int distance){
        PathResponse pathResponse = new PathResponse();
        pathResponse.stations = path;
        pathResponse.distance = distance;

        return pathResponse;
    }
}
