package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionLongDistanceException extends ApiException {

	public static final String MESSAGE = "역 사이에 새로운 역이 등록될 때 기존 역 사이 거리보다 길거나 같습니다.";

	public SectionLongDistanceException() {
		super(HttpStatus.BAD_REQUEST, MESSAGE);
	}
}
