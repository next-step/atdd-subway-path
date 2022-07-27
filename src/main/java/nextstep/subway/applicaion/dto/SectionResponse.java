package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
	private long lineId;
	private StationResponse upStation;
	private StationResponse downStation;
	private long distance;

	public SectionResponse(long lineId, Station upStation, Station downStation, long distance) {
		this.lineId = lineId;
		this.upStation = new StationResponse(upStation.getId(), upStation.getName());
		this.downStation = new StationResponse(downStation.getId(), downStation.getName());
		this.distance = distance;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section.getId(), section.getUpStation(), section.getDownStation(),
			section.getDistance().getDistance());
	}

	public long getLineId() {
		return lineId;
	}

	public StationResponse getUpStation() {
		return upStation;
	}

	public StationResponse getDownStation() {
		return downStation;
	}

	public long getDistance() {
		return distance;
	}
}
