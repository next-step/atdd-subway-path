package nextstep.subway.line.section;

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
                                   Object o,
                                   PositionType positionType) {
        return createApplyPosition(
                sectionList,
                positionType.applyTypes(),
                o);
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

    public boolean addingFirst() {
        return this.applyType.isApplyStart();
    }

    public int findingIndex() {
        return this.index;
    }

    public int applyIndex() {
        if (this.applyType.isApplyStart()) {
            return this.index;
        }
        return this.index + 1;
    }
}
