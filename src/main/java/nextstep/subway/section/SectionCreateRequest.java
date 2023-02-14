package nextstep.subway.section;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectionCreateRequest {

	private Long upStationId;
	private Long downStationId;
	private int distance;
}
