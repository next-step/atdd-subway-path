package nextstep.subway.applicaion.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

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

	public static LineResponse of(Line line) {
		return new LineResponse(
			line.getId(),
			line.getName(),
			line.getColor(),
			createStationResponses(line),
			line.getCreatedDate(),
			line.getModifiedDate());
	}

	private static List<StationResponse> createStationResponses(Line line) {
		if (line.sectionIsEmpty()) {
			return Collections.emptyList();
		}

		List<Station> stations = line.allSections().stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		stations.add(0, line.sectionByIndex(0).getUpStation());

		return stations.stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}
}

