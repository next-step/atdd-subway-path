package nextstep.subway.applicaion.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

}
