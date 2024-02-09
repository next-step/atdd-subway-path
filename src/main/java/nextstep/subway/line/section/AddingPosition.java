package nextstep.subway.line.section;

import java.util.List;
import java.util.stream.IntStream;

public class AddingPosition {
    private final boolean addingStart;
    private final int index;

    public AddingPosition(boolean addingStart,
                          int index) {
        this.addingStart = addingStart;
        this.index = index;
    }

    public static AddingPosition of(List<Section> sectionList, Section section) {

        AddingPosition addingPosition = addStart(IntStream.range(0, sectionList.size())
                .filter(i -> sectionList.get(i).isSameUpStationInputUpStation(section))
                .findFirst()
                .orElse(-1));

        if (addingPosition.isNotDefine()) {
            addingPosition = addEnd(IntStream.range(0, sectionList.size())
                    .filter(i -> sectionList.get(i).isSameDownStationInputDownStation(section))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("추가 할 구간을 찾지 못했습니다.")));
        }

        return addingPosition;
    }

    private static AddingPosition addStart(int index) {
        return new AddingPosition(true, index);
    }

    private static AddingPosition addEnd(int index) {
        return new AddingPosition(false, index);
    }

    public boolean isNotDefine() {
        return index < 0;
    }

    public boolean addingStart() {
        return this.addingStart;
    }

    public int findingIndex() {
        return this.index;
    }

    public int addingIndex() {
        if(this.addingStart) {
            return this.index;
        }
        return this.index + 1;
    }
}
