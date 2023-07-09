package subway.service.dto;

import lombok.Data;
import subway.domain.Station;

import java.util.Objects;

@Data
public class StationResponse {
	private Long id;
	private String name;

	public static StationResponse fromEntity(Station station) {
		if (Objects.isNull(station)) {
			return null;
		}

		final StationResponse response = new StationResponse();

		response.setId(station.getId());
		response.setName(station.getName());

		return response;
	}
}
