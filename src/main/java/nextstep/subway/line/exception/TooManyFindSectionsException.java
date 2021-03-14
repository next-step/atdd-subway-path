package nextstep.subway.line.exception;

import nextstep.subway.common.exception.BusinessException;

public class TooManyFindSectionsException extends BusinessException {

	private static final String FORMAT_MESSAGE = "%d 보다 많은 수의 구간이 발견되었습니다.";

	public TooManyFindSectionsException(int maxSize) {
		super(String.format(FORMAT_MESSAGE, maxSize));
	}
}
