package nextstep.subway.section;

import lombok.Getter;
import nextstep.subway.station.Station;

@Getter
public class SectionResponse {

	private Long id;
	private Station upStation;
	private Station downStation;
	private int distance;

	public SectionResponse(Section section) {
		this.id = section.getId();
		this.upStation = section.getUpStation();
		this.downStation = section.getDownStation();
		this.distance = section.getDistance();
	}
}
