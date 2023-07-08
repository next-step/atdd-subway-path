package subway.service.dto;

import lombok.Data;
import subway.domain.StationLine;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class StationLineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;

	public static StationLineResponse fromEntity(StationLine stationLine) {
		if (Objects.isNull(stationLine)) {
			return null;
		}

		final List<StationResponse> stations = stationLine.getAllStations()
			.stream()
			.map(StationResponse::fromEntity)
			.collect(Collectors.toList());

		final StationLineResponse response = new StationLineResponse();

		response.setId(stationLine.getLineId());
		response.setName(stationLine.getName());
		response.setColor(stationLine.getColor());
		response.setStations(stations);

		return response;
	}
}
