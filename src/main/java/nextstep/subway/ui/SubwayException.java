package nextstep.subway.ui;

import lombok.Getter;

@Getter
public class SubwayException extends IllegalArgumentException {
	private final String message;

	public SubwayException(SubwayErrorCode code) {
		this.message = code.getMessage();
	}
}
