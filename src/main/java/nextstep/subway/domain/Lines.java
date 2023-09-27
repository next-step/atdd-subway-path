package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Lines {
    private List<Line> lines;

    private Lines(List<Line> lines) {
        this.lines = lines;
    }

    public static Lines from(List<Line> lines) {
        return new Lines(lines);
    }

    public List<Section> getSections() {
        return lines.stream().flatMap(line -> line.getSections().getSections().stream()).collect(Collectors.toList());
    }
}
