package nextstep.subway.exception;

import static java.lang.String.format;

public class InvalidSectionSizeLimitExecption extends BadRequestException {
	private static final String EXCEPTION_MESSAGE = "Section cannot be lower than %s";

	public InvalidSectionSizeLimitExecption(int sectionSizeLimit) {
		super(format(EXCEPTION_MESSAGE, sectionSizeLimit));
	}
}
