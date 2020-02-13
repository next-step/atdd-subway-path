package atdd.station.dto;

import atdd.station.domain.Station;
import lombok.Getter;

@Getter
public class StationResponse {

	private Long id;
	private String name;

	public StationResponse(Station entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}
}
