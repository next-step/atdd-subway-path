package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathRequest {
	private List<LineResponse> lineResponses;
	private long source;
	private long target;

	public PathRequest(List<LineResponse> lineResponses, long source, long target) {
		this.lineResponses = lineResponses;
		this.source = source;
		this.target = target;
	}

	public List<LineResponse> getLineResponses() {
		return lineResponses;
	}

	public long getSource() {
		return source;
	}

	public long getTarget() {
		return target;
	}
}
