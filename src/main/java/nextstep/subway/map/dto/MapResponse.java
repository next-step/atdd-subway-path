package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponses;

public class MapResponse {

	protected MapResponse() {}

	private LineResponses lineResponses;

	public MapResponse(LineResponses lineResponses) {
		this.lineResponses = lineResponses;
	}

	public LineResponses getLineResponses() {
		return lineResponses;
	}
}
