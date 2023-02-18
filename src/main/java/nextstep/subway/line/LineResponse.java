package nextstep.subway.line;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import nextstep.subway.station.StationResponse;

@Getter
public class LineResponse {

	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;
	private int distance;

	public LineResponse(Line line) {
		this.id = line.getId();
		this.name = line.getName();
		this.color = line.getColor();
		this.stations = line.getOrderStation().stream().map(StationResponse::new).collect(Collectors.toList());
		this.distance = line.getDistance();
	}
}
