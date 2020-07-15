package nextstep.subway.line.dto;

import java.util.List;

public class LineResponses {

	private final List<LineResponse> lineResponses;

	public LineResponses(List<LineResponse> lineResponses) {
		this.lineResponses = lineResponses;
	}

	public List<LineResponse> getLineResponses() {
		return lineResponses;
	}
}
