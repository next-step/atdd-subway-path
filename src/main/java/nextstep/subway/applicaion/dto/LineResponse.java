package nextstep.subway.applicaion.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Line;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<StationResponse> stations;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public LineResponse(Long id, String name, String color, List<StationResponse> stations, LocalDateTime createdDate,
		LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.stations = stations;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<StationResponse> getStations() {
		return stations;
	}

	public static LineResponse of(Line line) {
		List<StationResponse> stations = line.allStations().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());

		return new LineResponse(
			line.getId(),
			line.getName(),
			line.getColor(),
			stations,
			line.getCreatedDate(),
			line.getModifiedDate());
	}
}

