package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    /**
     * Given 지하철 노선을 생성한다.
     * When 노선의 기존 구간 중간에 새로운 역을 추가 요청한다.
     * Then 지하철 구간 생성이 성공한다.
     */
    @DisplayName("지하철 구간 생성 - 역 사이에 새로운 역을 등록하는 경우")
    @Test
    void addSectionInTheMiddle() {
        // given
        Long 강남역_ID = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 역삼역_ID = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        Long 선릉역_ID = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        int 첫구간_거리 = 10;
        int 새로운_구간_거리 = 7;

        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "bg-color-green", 강남역_ID, 선릉역_ID, 첫구간_거리);
        Integer 이호선_ID = 지하철_노선_생성_응답.jsonPath().get("id");

        // when
        Map<String, String> 구간_생성_파라미터 = new HashMap<>();
        구간_생성_파라미터.put("upStationId", 강남역_ID.toString());
        구간_생성_파라미터.put("downStationId", 역삼역_ID.toString());
        구간_생성_파라미터.put("distance", String.valueOf(새로운_구간_거리));

        ExtractableResponse<Response> 지하철_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(이호선_ID.longValue(), 구간_생성_파라미터);

        // then
        assertThat(지하철_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성한다.
     * When 노선의 맨 앞에 새로운 역을 추가 요청한다. (새로운 역을 상행 종점으로 등록 요청한다.)
     * Then 지하철 구간 생성이 성공한다.
     */
    @DisplayName("지하철 구간 생성 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInTheFront() {
        // given
        Long 강남역_ID = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 역삼역_ID = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        Long 선릉역_ID = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        int 첫구간_거리 = 10;
        int 새로운_구간_거리 = 5;

        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "bg-color-green", 역삼역_ID, 선릉역_ID, 첫구간_거리);
        Integer 이호선_ID = 지하철_노선_생성_응답.jsonPath().get("id");

        // when
        Map<String, String> 구간_생성_파라미터 = new HashMap<>();
        구간_생성_파라미터.put("upStationId", 강남역_ID.toString());
        구간_생성_파라미터.put("downStationId", 역삼역_ID.toString());
        구간_생성_파라미터.put("distance", String.valueOf(새로운_구간_거리));

        ExtractableResponse<Response> 지하철_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(이호선_ID.longValue(), 구간_생성_파라미터);

        // then
        assertThat(지하철_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성한다.
     * When 노선의 맨 마지막에 새로운 역을 추가 요청한다. (새로운 역을 하행 종점으로 등록 요청한다.)
     * Then 지하철 구간 생성이 성공한다.
     */
    @DisplayName("지하철 구간 생성 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionAtTheEnd() {
        // given
        Long 강남역_ID = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        Long 역삼역_ID = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        Long 선릉역_ID = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        int 첫구간_거리 = 10;
        int 새로운_구간_거리 = 5;

        ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성_요청("2호선", "bg-color-green", 강남역_ID, 역삼역_ID, 첫구간_거리);
        Integer 이호선_ID = 지하철_노선_생성_응답.jsonPath().get("id");

        // when
        Map<String, String> 구간_생성_파라미터 = new HashMap<>();
        구간_생성_파라미터.put("upStationId", 역삼역_ID.toString());
        구간_생성_파라미터.put("downStationId", 선릉역_ID.toString());
        구간_생성_파라미터.put("distance", String.valueOf(새로운_구간_거리));

        ExtractableResponse<Response> 지하철_구간_추가_응답 = 지하철_노선에_지하철_구간_생성_요청(이호선_ID.longValue(), 구간_생성_파라미터);

        // then
        assertThat(지하철_구간_추가_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
