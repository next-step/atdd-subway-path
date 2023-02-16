package nextstep.subway.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class LineFixture {
    private static final Field idField = ReflectionUtils.findField(Line.class, "id");
    private static long testLineId = 1L;

    public static final Line 신분당선 = new Line("신분당선", "bg-red-900");
    
    static {
        지하철노선일련번호주입(신분당선);
    }

    private static void 지하철노선일련번호주입(Line line) {
        idField.setAccessible(true);
        ReflectionUtils.setField(idField, line, testLineId++);
        idField.setAccessible(false);
    }
}
