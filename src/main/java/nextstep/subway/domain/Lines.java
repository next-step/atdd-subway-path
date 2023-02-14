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

        for (Line line : values) {
            line.mergeSection(result);
        }

        return result;
    }
}
