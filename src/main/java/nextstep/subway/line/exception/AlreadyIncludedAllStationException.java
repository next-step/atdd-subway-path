package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class AlreadyIncludedAllStationException extends BusinessException {

	private static final String MESSAGE = "등록하려는 구간의 역이 모두 구간에 등록된 상태입니다.";

	public AlreadyIncludedAllStationException() {
		super(MESSAGE);
	}
}
