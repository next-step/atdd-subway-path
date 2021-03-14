package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class NotLastStationException extends BusinessException {
	private static final String FORMAT_MESSAGE = "%s 는/은 마지막 역이 아닙니다.";
	public NotLastStationException(String stationName) {
		super(String.format(FORMAT_MESSAGE, stationName));
	}
}
