package nextstep.subway.path;

import nextstep.subway.line.Line;

import java.util.List;

public class Path {
    private final List<Line> lines;

    public Path(List<Line> lines) {
        this.lines = lines;
    }
}
