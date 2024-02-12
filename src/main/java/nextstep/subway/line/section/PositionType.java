package nextstep.subway.line.section;

import java.util.List;

public enum PositionType {
    ADD_MIDDLE(List.of(ApplyType.ADD_FIRST, ApplyType.ADD_LAST)),
    DELETE_MEDDLE(List.of(ApplyType.DELETE_MIDDLE));

    private final List<ApplyType> applyTypes;

    PositionType(List<ApplyType> applyTypes) {
        this.applyTypes = applyTypes;
    }

    public List<ApplyType> applyTypes() {
        return this.applyTypes;
    }
}
