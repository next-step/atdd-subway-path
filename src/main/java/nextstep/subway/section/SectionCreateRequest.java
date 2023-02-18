package nextstep.subway.section;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.line.LineCreateRequest;

@Getter
@AllArgsConstructor
public class SectionCreateRequest {

	private Long upStationId;
	private Long downStationId;
	private int distance;

	public SectionCreateRequest(LineCreateRequest lineCreateRequest) {
		this.upStationId = lineCreateRequest.getUpStationId();
		this.downStationId = lineCreateRequest.getDownStationId();
		this.distance = lineCreateRequest.getDistance();
	}
}
