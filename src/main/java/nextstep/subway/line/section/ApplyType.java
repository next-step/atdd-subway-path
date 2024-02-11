package nextstep.subway.line.section;

import nextstep.subway.station.Station;

import java.util.List;

public enum ApplyType {
    ADD_FIRST((sectionList, i, input) -> sectionList.get(i).isSameUpStationInputUpStation((Section) input)),
    ADD_LAST((sectionList, i, input) -> sectionList.get(i).isSameDownStationInputDownStation((Section) input)),
    ADD_MIDDLE((sectionList, i, input) -> false),
    DELETE_FIRST((sectionList, i, input) -> false),
    DELETE_LAST((sectionList, i, input) -> false),
    DELETE_MIDDLE((sectionList, i, input) -> sectionList.get(i).isSameDownStation((Station) input));

    private final TriFunction<List<Section>, Integer, Object, Boolean> operation;

    ApplyType(TriFunction<List<Section>, Integer, Object, Boolean> operation) {
        this.operation = operation;
    }

    public boolean apply(List<Section> sections,
                         Integer integer,
                         Object o) {
        return this.operation.apply(sections, integer, o);
    }

    public boolean isApplyMiddle() {
        return this == ApplyType.ADD_MIDDLE || this == ApplyType.DELETE_MIDDLE;
    }

    public boolean isApplyStart() {
        return this == ApplyType.ADD_FIRST || this == ApplyType.DELETE_FIRST;
    }
}
