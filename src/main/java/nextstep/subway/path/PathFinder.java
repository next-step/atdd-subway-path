package nextstep.subway.path;

import nextstep.subway.line.Line;

import java.util.List;

public class PathFinder {
    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }

    public Path shortcut() {
        return null;
    }
}
