package nextstep.subway.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.fixture.SectionFixture.createSection;

public class LineFixture {

    public static Line createLine() {
        return createLine(0L);
    }

    public static Line createLine(Long id) {
        return Line.of(id, "이름", "색깔");
    }

    public static Line createLineWithSection(Long upStationId, Long downStationId) {
        return createLineWithSection(0L, upStationId, downStationId);
    }

    public static Line createLineWithSection(Long lindId, Long upStationId, Long downStationId) {
        Line line = createLine(lindId);
        Section section = createSection(upStationId, downStationId);
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
