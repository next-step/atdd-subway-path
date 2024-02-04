package nextstep.subway.common.fixture;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.util.ReflectionUtils;

public class LineFactory {
    private LineFactory() {

    }

    public static Line createLine(final String name, final String color, final Section section) {
        return new Line(name, color, section);
    }

    public static Line createLine(final Long id, final String name, final String color, final Section section) {
        final Line line = createLine(name, color, section);
        ReflectionUtils.injectIdField(line, id);
        return line;
    }

}
