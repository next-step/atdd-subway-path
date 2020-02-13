package atdd.line.dto;

import atdd.line.domain.Line;
import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FindLineResponse {

	private Long id;
	private String name;
	private String startTime;
	private String endTime;
	private String intervalTime;
	private List<Station> stations;

	@Builder
	public FindLineResponse(final Line line, final List<Station> stations) {
		this.id = line.getId();
		this.name = line.getName();
		this.startTime = line.getStartTime();
		this.endTime = line.getEndTime();
		this.intervalTime = line.getIntervalTime();
		this.stations = stations;
	}
}
