package nextstep.subway.path.dto.response;

import lombok.Getter;
import nextstep.subway.station.dto.response.StationResponse;

import java.util.List;

@Getter
public class PathResponse {

    List<StationResponse> stations;

    Integer distance;

}
