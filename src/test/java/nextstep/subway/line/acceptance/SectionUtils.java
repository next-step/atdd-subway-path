package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionTestRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.commons.AssertionsUtils.삭제요청_성공;
import static nextstep.subway.commons.AssertionsUtils.생성요청_성공;
import static nextstep.subway.commons.RestAssuredUtils.delete;
import static nextstep.subway.commons.RestAssuredUtils.post;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionUtils {

    private SectionUtils() {}

    public static ExtractableResponse<Response> 지하철노선_구간생성_요청(SectionTestRequest request) {

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(request.getUpStationId()));
        params.put("downStationId", String.valueOf(request.getDownStationId()));
        params.put("distance", String.valueOf(request.getDistance()));

        return post("/lines/" + request.getLineId() + "/sections", params);
    }

    public static void 지하철노선_하행종점역_검증(long downStationId, ExtractableResponse<Response> lineResponse) {
        List<Integer> stationIds = lineResponse.jsonPath().getList("stations.id");
        assertThat(Long.valueOf(stationIds.get(stationIds.size() - 1))).isEqualTo(downStationId);
    }

    public static void 지하철노선_상행종점역_검증(long upStationId, ExtractableResponse<Response> lineResponse) {
        List<Integer> stationIds = lineResponse.jsonPath().getList("stations.id");
        assertThat(Long.valueOf(stationIds.get(0))).isEqualTo(upStationId);
    }

    public static void 지하철노선_구간생성_요청_성공(ExtractableResponse<Response> sectionResponse,
                                        ExtractableResponse<Response> lineResponse) {

        생성요청_성공(sectionResponse);
        assertThat(lineResponse.jsonPath().getList("stations").size()).isEqualTo(3);
    }

    public static ExtractableResponse<Response> 지하철역_삭제_요청(Long lindId, Long stationId) {
        return delete("/lines/" + lindId + "/sections?stationId=" + stationId);
    }

    public static void 지하철역_삭제_요청_성공(ExtractableResponse<Response> deleteResponse,
                                     ExtractableResponse<Response> findResponse) {
        삭제요청_성공(deleteResponse);
        assertThat(findResponse.as(LineResponse.class).getStations()).hasSize(2);
    }
}
