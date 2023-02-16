package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PathResponse {
    private List<StationResponse> stations;
    private long distance;
}
