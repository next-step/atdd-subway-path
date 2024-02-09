package nextstep.subway.line.section;

import nextstep.subway.station.Station;

import java.util.List;
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

        ApplyPosition applyPosition = addFirst(IntStream.range(0, sectionList.size())
                .filter(i -> sectionList.get(i).isSameUpStationInputUpStation(section))
                .findFirst()
                .orElse(-1));

        if (applyPosition.isNotDefine()) {
            applyPosition = addLast(IntStream.range(0, sectionList.size())
                    .filter(i -> sectionList.get(i).isSameDownStationInputDownStation(section))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("추가 할 구간을 찾지 못했습니다.")));
        }

        return applyPosition;
    }

    public static ApplyPosition of(List<Section> sectionList,
                                   Station station) {
        return deleteMiddle(IntStream.range(0, sectionList.size() - 1)
                .filter(i -> sectionList.get(i).isSameDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 역을 찾지 못했습니다.")));
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
