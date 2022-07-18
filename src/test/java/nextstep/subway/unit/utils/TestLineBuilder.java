package nextstep.subway.unit.utils;

import nextstep.subway.domain.Line;
import org.springframework.test.util.ReflectionTestUtils;

public class TestLineBuilder {

    private Long id;
    private String name;
    private String color;

    public static TestLineBuilder aLine() {
        return new TestLineBuilder();
    }

    public TestLineBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public TestLineBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TestLineBuilder color(String color) {
        this.color = color;
        return this;
    }

    public Line build() {
        var line = new Line(name, color);
        ReflectionTestUtils.setField(line, "id", id);
        return line;
    }

}
