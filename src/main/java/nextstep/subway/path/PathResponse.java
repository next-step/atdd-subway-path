package nextstep.subway.path;

import java.util.List;

import lombok.Getter;
import nextstep.subway.station.StationResponse;

@Getter
public class PathResponse {

	private List<StationResponse> stations;
	private int distance;
}
