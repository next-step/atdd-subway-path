package nextstep.subway.map;

import java.util.Collections;
import java.util.List;

import nextstep.subway.line.dto.LineResponse;

public class MapResponseDto {
	private final List<LineResponse> lines;

	public MapResponseDto(final List<LineResponse> lines) {
		this.lines = Collections.unmodifiableList(lines);
	}

	public final List<LineResponse> getLines() {
		return Collections.unmodifiableList(lines);
	}
}
