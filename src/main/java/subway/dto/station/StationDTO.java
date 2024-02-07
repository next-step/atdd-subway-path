package subway.dto.station;

import subway.station.Station;

public class StationDTO {
	private final Long id;
	private final String name;

	private StationDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public static StationDTO of(Station station) {
		return new StationDTO(station.getId(), station.getName());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
