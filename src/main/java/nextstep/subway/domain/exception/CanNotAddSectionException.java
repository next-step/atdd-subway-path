package nextstep.subway.domain.exception;

import nextstep.subway.domain.Section;

public class CanNotAddSectionException extends IllegalStateException {
    public CanNotAddSectionException(Section section) {
        super(String.format("can not add section up station : %s and down station : %s", section.getUpStation().getName(), section.getDownStation().getName()));
    }
}
