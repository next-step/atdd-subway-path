package nextstep.subway.path.exception;

import nextstep.subway.common.exception.BusinessException;

public class NotExistPathException extends BusinessException {

	private static final String MESSAGE = "경로가 존재하지 않습니다.";

	public NotExistPathException() {
		super(MESSAGE);
	}
}
