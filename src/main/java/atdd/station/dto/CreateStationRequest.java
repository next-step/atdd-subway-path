package atdd.station.dto;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateStationRequest {

	private String name;

	@Builder
	public CreateStationRequest(final String name) {
		this.name = name;
	}

	public Station toEntity() {
		return Station.builder()
			.name(name)
			.build();
	}
}
