package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.controller.dto.LineCreateRequest;
import nextstep.subway.controller.dto.LineUpdateRequest;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.helper.JsonPathUtils.*;
import static nextstep.subway.acceptance.LineApiRequester.*;
import static nextstep.subway.acceptance.StationApiRequester.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
public class LineAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Nested
    class 지하철_노선_생성_테스트 {

        long upStationId;
        long downStationId;

        @BeforeEach
        void setup() {
            upStationId = getLongPath(createStation("수원역"), "id");
            downStationId = getLongPath(createStation("고색역"), "id");
        }

        // TODO: 유효하지 않은 값에 대한 검증 추가
        @DisplayName("지하철 노선을 생성하면, 생성한 노선을 찾을 수 있다")
        @Test
        void createSubwayLine() {
            // given
            LineCreateRequest 수인분당선 = new LineCreateRequest(
                "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
            );

            // when
            ExtractableResponse<Response> response = createLine(수인분당선);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(getStringPath(response.body(), "name")).isEqualTo(수인분당선.getName());

            long createdSubwayLineId = getSubwayLineId(response);
            assertThat(createdSubwayLineId).isNotNull();

            // then
            ExtractableResponse<Response> getSubwayLinesResponse = getLineById(createdSubwayLineId);

            assertThat(getSubwayLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(getSubwayLineId(getSubwayLinesResponse)).isEqualTo(createdSubwayLineId);
        }
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Nested
    class 지하철_노선_목록_조회_테스트 {

        long 수인분당선_upStationId;
        long 수인분당선_downStationId;
        long 신분당선_upStationId;
        long 신분당선_downStationId;

        @BeforeEach
        void setup() {
            수인분당선_upStationId = getLongPath(createStation("수원역"), "id");
            수인분당선_downStationId = getLongPath(createStation("고색역"), "id");

            신분당선_upStationId = getLongPath(createStation("강남역"), "id");
            신분당선_downStationId = getLongPath(createStation("양재역"), "id");
        }

        @DisplayName("생성한 지하철 노선을 모두 조회할 수 있다")
        @Test
        void will_return_created_subway_lines() {
            // given
            LineCreateRequest 수인분당선 = new LineCreateRequest(
                "수인분당선", "bg-yellow-600", 수인분당선_upStationId, 수인분당선_downStationId, 10
            );
            LineCreateRequest 신분당선 = new LineCreateRequest(
                "신분당선", "bg-red-600", 신분당선_upStationId, 신분당선_downStationId, 5
            );

            createLine(수인분당선);
            createLine(신분당선);

            // when
            ExtractableResponse<Response> getSubwayLinesResponse = getAllLines();

            assertThat(getSubwayLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(getListPath(getSubwayLinesResponse.body(), "name", String.class))
                .containsExactly(수인분당선.getName(), 신분당선.getName());
        }
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Nested
    class 지하철_노선_조회_테스트 {

        long upStationId;
        long downStationId;

        @BeforeEach
        void setup() {
            upStationId = getLongPath(createStation("수원역"), "id");
            downStationId = getLongPath(createStation("고색역"), "id");
        }

        // TODO: 찾을 수 없는 노선 ID가 주어지는 경우 검증
        @DisplayName("생성한 지하철 노선을 조회하면 해당 노선의 정보를 응답받는다")
        @Test
        void will_return_subway_line() {
            // given
            LineCreateRequest 수인분당선 = new LineCreateRequest(
                "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
            );

            ExtractableResponse<Response> createSubwayResponse = createLine(수인분당선);
            long createdSubwayLineId = getSubwayLineId(createSubwayResponse);

            // when
            ExtractableResponse<Response> getSubwayLineResponse = getLineById(createdSubwayLineId);

            // then
            assertThat(getSubwayLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(getSubwayLineId(getSubwayLineResponse)).isEqualTo(createdSubwayLineId);
        }
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Nested
    class 지하철_노선_수정_테스트 {

        long upStationId;
        long downStationId;

        @BeforeEach
        void setup() {
            upStationId = getLongPath(createStation("수원역"), "id");
            downStationId = getLongPath(createStation("고색역"), "id");
        }

        // TODO: 유효하지 않은 수정 데이터 검증
        @DisplayName("생성한 지하철 노선을 수정하면 해당 지하철 노선 정보는 수정된다")
        @Test
        void will_return_updated_subway_line_data() {
            // given
            LineCreateRequest 수인분당선 = new LineCreateRequest(
                "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
            );

            ExtractableResponse<Response> createSubwayResponse = createLine(수인분당선);
            long createdSubwayLineId = getSubwayLineId(createSubwayResponse);

            // when
            LineUpdateRequest updateRequest = new LineUpdateRequest("수인분당당선", "bg-brown-600");

            ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", createdSubwayLineId)
                .when().put("/lines/{lineId}")
                .then().log().all()
                .extract();

            assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

            // when
            ExtractableResponse<Response> getSubwayLineResponse = getLineById(createdSubwayLineId);

            // then
            assertThat(getSubwayLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(getStringPath(getSubwayLineResponse.body(), "name"))
                .isEqualTo(updateRequest.getName());
            assertThat(getStringPath(getSubwayLineResponse.body(), "color"))
                .isEqualTo(updateRequest.getColor());
        }
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Nested
    class 지하철_노선_삭제_테스트 {

        long upStationId;
        long downStationId;

        @BeforeEach
        void setup() {
            upStationId = getLongPath(createStation("수원역"), "id");
            downStationId = getLongPath(createStation("고색역"), "id");
        }

        @DisplayName("생성한 지하철 노선을 삭제하면 해당 지하철 노선 정보는 삭제된다")
        @Test
        void will_delete_subway_line() {
            // given
            LineCreateRequest 수인분당선 = new LineCreateRequest(
                "수인분당선", "bg-yellow-600", upStationId, downStationId, 10
            );

            ExtractableResponse<Response> createSubwayResponse = createLine(수인분당선);
            long createdSubwayLineId = getSubwayLineId(createSubwayResponse);

            // when
            ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .pathParam("lineId", createdSubwayLineId)
                .when().delete("/lines/{lineId}")
                .then().log().all()
                .extract();

            assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

            // then
            ExtractableResponse<Response> getSubwayLinesResponse = getAllLines();

            assertThat(getSubwayLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(getSubwayLineIds(getSubwayLinesResponse)).noneMatch(id -> id.equals(createdSubwayLineId));
        }
    }

    /** 주어진 응답값에서 식별자를 추출해 반환합니다 */
    private long getSubwayLineId(final ExtractableResponse<Response> response) {
        return getLongPath(response.body(), "id");
    }

    /** 주어진 응답값에서 식별자목록을 추출해 반환합니다 */
    private List<Long> getSubwayLineIds(final ExtractableResponse<Response> response) {
        return getListPath(response.body(), "id", Long.class);
    }
}
