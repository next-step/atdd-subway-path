package nextstep.subway.station;

import lombok.Getter;

@Getter
public class StationResponse {
	private Long id;
	private String name;

	public StationResponse(Station station) {
		this.id = station.getId();
		this.name = station.getName();
	}
}
