package nextstep.subway.line.section;

import nextstep.subway.station.Station;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class ApplyPosition {
    private final ApplyType applyType;
    private final int index;

    public ApplyPosition(ApplyType applyType,
                         int index) {
        this.applyType = applyType;
        this.index = index;
    }

    public static ApplyPosition of(List<Section> sectionList,
                                   Section section) {
        return createApplyPosition(
                sectionList,
                List.of(ApplyType.ADD_FIRST, ApplyType.ADD_LAST),
                section);
    }

    public static ApplyPosition of(List<Section> sectionList,
                                   Station station) {
        return createApplyPosition(
                sectionList,
                List.of(ApplyType.DELETE_MIDDLE),
                station
        );
    }

    private static ApplyPosition createApplyPosition(
            List<Section> sectionList,
            List<ApplyType> applyTypes,
            Object o) {

        for (ApplyType type : applyTypes) {
            OptionalInt maybeIndex = findIndex(sectionList.size(), sectionList, o, type);
            if (maybeIndex.isPresent()) {
                return new ApplyPosition(type, maybeIndex.getAsInt());
            }
        }

        throw new IllegalArgumentException("적절한 구간을 찾지 못했습니다.");
    }

    private static OptionalInt findIndex(int size,
                                         List<Section> sectionList,
                                         Object input,
                                         ApplyType applyType) {
        return IntStream.range(0, size)
                .filter(i -> applyType.apply(sectionList, i, input))
                .findFirst();
    }

    private static ApplyPosition addFirst(int index) {
        return new ApplyPosition(ApplyType.ADD_FIRST, index);
    }

    private static ApplyPosition addLast(int index) {
        return new ApplyPosition(ApplyType.ADD_LAST, index);
    }

    private static ApplyPosition deleteMiddle(int index) {
        return new ApplyPosition(ApplyType.DELETE_MIDDLE, index);
    }

    public boolean isNotDefine() {
        return index < 0;
    }

    public boolean addingFirst() {
        return this.applyType.isAppyStart();
    }

    public int findingIndex() {
        return this.index;
    }

    public int applyIndex() {
        if (this.applyType.isAppyStart()) {
            return this.index;
        }
        return this.index + 1;
    }
}
