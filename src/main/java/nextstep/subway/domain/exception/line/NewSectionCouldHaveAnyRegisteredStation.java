package nextstep.subway.domain.exception.line;

import nextstep.subway.domain.exception.BusinessException;

public class NewSectionCouldHaveAnyRegisteredStation extends BusinessException {

    private static final String MESSAGE = LineErrorMessage.NEW_SECTION_COULD_HAVE_ANY_REGISTERED_STATION.getMessage();

    public NewSectionCouldHaveAnyRegisteredStation() {
        super(MESSAGE);
    }
}
