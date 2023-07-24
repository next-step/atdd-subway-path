package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionNotIncludedException extends ApiException {

	public static final String MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어 있지 않습니다.";

	public SectionNotIncludedException() {
		super(HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
