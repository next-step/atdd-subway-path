package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class NotExistSameStationException extends BusinessException {

	public static final String MESSAGE = "구간을 나누려면 상행역 또는 하행역이 같아야 합니다.";

	public NotExistSameStationException() {
		super(MESSAGE);
	}
}
