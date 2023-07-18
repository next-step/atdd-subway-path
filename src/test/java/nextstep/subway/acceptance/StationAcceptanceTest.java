package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.handler.StationHandler.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");

        // then
        지하철_역_생성됨(강남역);

        // then
        List<String> 조회된_지하철_역_이름_목록 = 지하철_역_이름_목록_조회_요청();
        지하철_역_이름_조회됨(조회된_지하철_역_이름_목록);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> 지하철_역_목록_조회_결과 = 지하철_역_목록_조회_요청();

        // then
        두개의_지하철_역_응답됨(지하철_역_목록_조회_결과);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");

        // when
        지하철_역_삭제_요청(강남역);

        // then
        List<String> 조회된_지하철_역_이름_목록 = 지하철_역_이름_목록_조회_요청();
        지하철_역_삭제됨(조회된_지하철_역_이름_목록);
    }

    private ExtractableResponse<Response> 지하철_역_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    private List<String> 지하철_역_이름_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract()
                    .jsonPath().getList("name", String.class);
    }

    private void 지하철_역_삭제_요청(ExtractableResponse<Response> response) {
        String location = response.header("location");
        RestAssured
                .given().log().all()
                .when()
                    .delete(location)
                .then().log().all()
                .extract();
    }

    private void 지하철_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_역_이름_조회됨(List<String> stationNames) {
        assertThat(stationNames).containsAnyOf("강남역");
    }

    private void 두개의_지하철_역_응답됨(ExtractableResponse<Response> response) {
        List<StationResponse> stations = response.jsonPath().getList(".", StationResponse.class);
        assertThat(stations).hasSize(2);
    }

    private void 지하철_역_삭제됨(List<String> stationNames) {
        assertThat(stationNames).doesNotContain("강남역");
    }
}