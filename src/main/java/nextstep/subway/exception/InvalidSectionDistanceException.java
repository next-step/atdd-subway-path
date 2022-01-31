package nextstep.subway.exception;

import nextstep.subway.domain.Distance;

import static java.lang.String.format;

public class InvalidSectionDistanceException extends BadRequestException {

	public InvalidSectionDistanceException(Distance newSectionDistance, Distance oldSectionDistance) {
		super(format("new section distance '%s' must be lower than old section distance '%s",
				newSectionDistance.getDistance(),
				oldSectionDistance.getDistance()));
	}
}
