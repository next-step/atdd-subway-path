package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionNotIncludedException extends ApiException {

	public static final String MESSAGE = "노선에 등록되어 있지 않은 역입니다.";

	public SectionNotIncludedException() {
		super(HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
