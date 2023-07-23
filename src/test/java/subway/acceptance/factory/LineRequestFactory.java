package subway.acceptance.factory;

import static subway.factory.SubwayNameFactory.*;

import java.util.HashMap;
import java.util.Map;
import subway.dto.LineRequest;

public class LineRequestFactory {

    // {key: 구간 이름, value: 구간값 { key: 구간 필드, value: 구간 필드값 }}
    private static final Map<String, LineRequest> LINE_MAP = new HashMap<>() {{
        put(신분당선, LineRequest.builder()
            .name(신분당선)
            .color("bg-red-600")
            .upStationId(1L)
            .downStationId(2L)
            .distance(10L)
            .build());
        put(우이신설선, LineRequest.builder()
            .name(우이신설선)
            .color("bg-green-600")
            .upStationId(3L)
            .downStationId(4L)
            .distance(20L)
            .build());
        put(공항철도선, LineRequest.builder()
            .name(공항철도선)
            .color("bg-blue-600")
            .upStationId(5L)
            .downStationId(6L)
            .distance(30L)
            .build());

    }};

    public static LineRequest create(String name) {
        return LINE_MAP.get(name);
    }

    public static LineRequest createNameAndColorUpdateParams(String name, String color) {
        return LineRequest.builder()
            .name(name)
            .color(color)
            .build();
    }
}
