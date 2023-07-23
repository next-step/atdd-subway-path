package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.lang.reflect.Field;

public class SectionFixture {

    static Long id = 1L;

    public static Section 지하철_구간_생성(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line,upStation,downStation,distance);

        Class<Section> sectionClass = Section.class;
        Field sectionIdField = null;
        try {
            sectionIdField = sectionClass.getDeclaredField("id");
            sectionIdField.setAccessible(true);
            sectionIdField.set(section, id++);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return section;
    }
}
