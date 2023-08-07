package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;
}
