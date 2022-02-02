package nextstep.subway.exception;

import static java.lang.String.format;

public class SourceTargetEqualException extends BadRequestException {

	public SourceTargetEqualException(String sourceName) {
		super(format("The Source and the target station('%s') are the same", sourceName));
	}
}
