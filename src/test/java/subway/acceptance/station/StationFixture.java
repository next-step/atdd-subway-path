package subway.acceptance.station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationFixture {
    public static final Map<String, Long> stationsMap = new HashMap<>();

    public static void 기본_역_생성() {
        List.of("교대역", "강남역", "역삼역", "선릉역", "삼성역", "잠실역", "강변역", "건대역", "성수역", "왕십리역", // 2호선
                        "남부터미널역", "양재역", // 3호선
                        "신논현역", "양재시민의숲역") // 신분당선
                .forEach(StationSteps::역_생성_API);
        var response = StationSteps.역_목록_조회_API();
        List<Map<String, Object>> jsonResponse = response.jsonPath().getList("$");
        for (Map<String, Object> station : jsonResponse) {
            stationsMap.put((String) station.get("name"), ((Number) station.get("id")).longValue());
        }
    }

    public static Long getStationId(String name) {
        return stationsMap.get(name);
    }
}
