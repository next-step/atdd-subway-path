package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse {
	private StationResponse upStation;
	private StationResponse downStation;
	private long distance;

	public SectionResponse(Station upStation, Station downStation, long distance) {
		this.upStation = new StationResponse(upStation.getId(), upStation.getName());
		this.downStation = new StationResponse(downStation.getId(), downStation.getName());
		this.distance = distance;
	}

	public static SectionResponse of(Section section) {
		return new SectionResponse(section.getUpStation(), section.getDownStation(),
			section.getDistance());
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
