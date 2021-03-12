package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class AlreadyExistDownStationException extends BusinessException {

	public static final String MESSAGE = "하행역이 이미 등록되어 있습니다.";

	public AlreadyExistDownStationException() {
		super(MESSAGE);
	}
}
