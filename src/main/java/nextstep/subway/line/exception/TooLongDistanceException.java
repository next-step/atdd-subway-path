package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class TooLongDistanceException extends BusinessException {

	private static final String MESSAGE = "중간에 추가되는 구간의 길이는 원래 구간의 길이보다 크거나 같을 수 없습니다.";

	public TooLongDistanceException() {
		super(MESSAGE);
	}
}
