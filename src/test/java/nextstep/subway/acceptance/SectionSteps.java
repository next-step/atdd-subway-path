package nextstep.subway.acceptance;

import java.util.HashMap;
import java.util.Map;

public class SectionSteps {

	public static Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
		Map<String, String> lineCreateParams;
		lineCreateParams = new HashMap<>();
		lineCreateParams.put("name", "신분당선");
		lineCreateParams.put("color", "bg-red-600");
		lineCreateParams.put("upStationId", upStationId + "");
		lineCreateParams.put("downStationId", downStationId + "");
		lineCreateParams.put("distance", 10 + "");
		return lineCreateParams;
	}

	public static Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
		Map<String, String> params = new HashMap<>();
		params.put("upStationId", upStationId + "");
		params.put("downStationId", downStationId + "");
		params.put("distance", distance + "");
		return params;
	}
}
