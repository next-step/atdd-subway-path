package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class TooLowLengthSectionsException extends BusinessException {

	private static final String FORMAT_MESSAGE = "구간의 길이가 너무 짧습니다.(최소 %d)";

	public TooLowLengthSectionsException(int size) {
		super(String.format(FORMAT_MESSAGE, size));
	}
}
