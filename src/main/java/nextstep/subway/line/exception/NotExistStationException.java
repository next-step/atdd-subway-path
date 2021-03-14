package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class NotExistStationException extends BusinessException {

	private static final String MESSAGE = "구간에 등록된 역이 없습니다.";

	public NotExistStationException() {
		super(MESSAGE);
	}
}
