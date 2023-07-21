package nextstep.subway.fixture.unit.entity;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import nextstep.subway.entity.Line;

public class LineFixture {

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public static Line of(Long id, String name, String color) {

        Line line = spy(new Line(name, color));
        when(line.getId()).thenReturn(id);

        return line;
    }
}
