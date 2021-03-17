package nextstep.subway.path.exception;

import nextstep.subway.common.exception.BusinessException;

public class SameStationException extends BusinessException {

	private static final String MESSAGE = "출발역과 종착역은 같을 수 없습니다.";
	public SameStationException() {
		super(MESSAGE);
	}
}
