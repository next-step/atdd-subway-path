package nextstep.subway.fixture;

import nextstep.subway.domain.Section;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static nextstep.subway.fixture.LineFixture.신분당선;
import static nextstep.subway.fixture.StationFixture.*;

public class SectionFixture {
    private static final Field sectionIdField = ReflectionUtils.findField(Section.class, "id");
    private static long testSectionId = 1L;

    public static final Section 강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 10);
    public static final Section 양재_청계산_구간 = new Section(신분당선, 양재역, 청계산역, 10);
    public static final Section 청계산_정자_구간 = new Section(신분당선, 청계산역, 정자역, 10);
    public static final Section 양재_양재시민의숲_구간 = new Section(신분당선, 양재역, 양재시민의숲역, 10);

    static {
        지하철구간일련번호주입(강남_양재_구간);
        지하철구간일련번호주입(양재_청계산_구간);
        지하철구간일련번호주입(청계산_정자_구간);
        지하철구간일련번호주입(양재_양재시민의숲_구간);
    }

    private static void 지하철구간일련번호주입(Section section) {
        sectionIdField.setAccessible(true);
        ReflectionUtils.setField(sectionIdField, section, testSectionId++);
        sectionIdField.setAccessible(false);
    }
}
