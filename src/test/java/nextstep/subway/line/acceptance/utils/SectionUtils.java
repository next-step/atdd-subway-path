package nextstep.subway.line.acceptance.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.acceptance.dto.SectionRequest;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.commons.AssertionsUtils.생성요청_성공;
import static nextstep.subway.commons.RestAssuredUtils.delete_요청;
import static nextstep.subway.commons.RestAssuredUtils.post_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionUtils {

    private SectionUtils() {}

    public static ExtractableResponse<Response> 지하철노선_구간생성_요청(Long lineId, SectionRequest request) {

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(request.getUpStationId()));
        params.put("downStationId", String.valueOf(request.getDownStationId()));
        params.put("distance", String.valueOf(request.getDistance()));

        return post_요청("/lines/" + lineId + "/sections", params);
    }

    public static void 지하철노선_구간생성_요청_성공(ExtractableResponse<Response> sectionResponse,
                                        ExtractableResponse<Response> lineResponse) {

        생성요청_성공(sectionResponse);
        assertThat(lineResponse.jsonPath().getList("stations").size()).isEqualTo(3);

    }

    public static ExtractableResponse<Response> 지하철노선_역삭제_요청(Long lineId, Long stationId) {
        return delete_요청("/lines/" + lineId + "/sections?stationId=" + stationId);
    }

}
