package nextstep.subway.applicaion.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PathResponse {
    private List<PathStationResponse> stations = new ArrayList<>();
    private Long distance;

    public PathResponse() {

    }
}
