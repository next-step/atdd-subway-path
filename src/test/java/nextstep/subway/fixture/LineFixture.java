package nextstep.subway.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.SectionFixture.createSection;

public class LineFixture {

    public static Line createLine() {
        return Line.of("이름", "색깔");
    }

    public static Line createLine(Long upStationId, Long downStationId) {
        Line line = createLine();
        Section section = createSection(0L, 1L);
        line.addSection(section);
        return line;
    }

    public static Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams = createLineCreateParams("신분당선", "bg-red-600", upStationId, downStationId, distance);
        return lineCreateParams;
    }

    public static Map<String, String> createLineCreateParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", color);
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }
}
