package nextstep.subway.applicaion.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;
}
