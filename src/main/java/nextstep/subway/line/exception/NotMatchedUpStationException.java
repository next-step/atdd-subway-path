package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class NotMatchedUpStationException extends BusinessException {

	public static final String MESSAGE = "상행역은 하행 종점역이어야 합니다.";

	public NotMatchedUpStationException() {
		super(MESSAGE);
	}
}
