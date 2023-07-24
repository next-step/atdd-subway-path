package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.lang.reflect.Field;

public class LineFixture {
    private static Long id = 1L;

    public static Line 지하철_노선_생성(String name, String color, Station upStation, Station downStation, int distance) {
        Line line = new Line(name,color,upStation,downStation,distance);

        try {
            Class<Line> lineClass = Line.class;
            Field lineIdField = lineClass.getDeclaredField("id");
            lineIdField.setAccessible(true);
            lineIdField.set(line, id++);

            Class<Section> sectionClass = Section.class;
            Field sectionField = sectionClass.getDeclaredField("id");
            sectionField.setAccessible(true);
            for (Section section : line.getSections()) {
                sectionField.set(section, SectionFixture.id++);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return line;
    }
}
