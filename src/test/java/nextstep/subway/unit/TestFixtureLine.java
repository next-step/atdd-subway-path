package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

public class TestFixtureLine {

    public static Line 노선_생성(final Long id, final String name, final String color, final Station upStation, Station downStation, final Integer distance) {
        final Line line = new Line(name, color, upStation, downStation, distance);
        reflectionById(id, line);
        return line;
    }

    public static Line 노선_생성(final Long id, final String name, final String color, final List<Section> sections) {
        final Line line = new Line(name, color, new Sections(sections));
        reflectionById(id, line);
        return line;
    }

    public static Sections 노선_구간들(final Line line) {
        return new Sections(line.getSections().getSections());
    }


    public static Section 구간_생성(final Long id, final Station upStation, final Station downStation, final Integer distance) {
        final Section section = new Section(upStation, downStation, distance);
        reflectionById(id, section);
        return section;
    }

    public static void reflectionById(final Long id, final Object object) {
        ReflectionTestUtils.setField(object, "id", id);
    }
}
