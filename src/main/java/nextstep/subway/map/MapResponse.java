package nextstep.subway.map;

import nextstep.subway.line.dto.LineResponses;

public class MapResponse {

	private final LineResponses lineResponses;

	public MapResponse(LineResponses lineResponses) {
		this.lineResponses = lineResponses;
	}

	public LineResponses getLineResponses() {
		return lineResponses;
	}
}
