package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lines {

    private final Set<Line> values;

    public static Lines from(Set<Line> lineSet) {
        return new Lines(lineSet);
    }

    public int size() {
        return values.size();
    }

    public List<Section> mergeSections() {
        List<Section> result = new ArrayList<>();
        values.forEach(line -> line.mergeSection(result)); // 실제로는 result에 추가하는데 의미상 반대로 읽히나?
        return result;
    }
}
