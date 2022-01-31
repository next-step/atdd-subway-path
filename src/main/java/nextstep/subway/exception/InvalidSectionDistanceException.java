package nextstep.subway.exception;

import nextstep.subway.domain.Distance;

import static java.lang.String.format;

public class InvalidSectionDistanceException extends BadRequestException {

	private static final String EXCEPTION_MESSAGE = "new section distance '%s' must be lower than old section distance '%s";

	public InvalidSectionDistanceException(Distance newSectionDistance, Distance oldSectionDistance) {
		super(format(EXCEPTION_MESSAGE,
				newSectionDistance.getDistance(),
				oldSectionDistance.getDistance()));
	}
}
