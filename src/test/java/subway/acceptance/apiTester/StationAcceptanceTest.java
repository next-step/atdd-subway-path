package subway.acceptance.apiTester;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import subway.acceptance.utils.AbstractAcceptanceTest;

import java.util.List;

/**
 * 지하철 관련 api 스펙을 함수로 정의한다.
 */
@DisplayName("지하철역 관련 기능")
public abstract class StationAcceptanceTest extends AbstractAcceptanceTest {

    /**
     * 지하철 역 생성 요청을 합니다
     * @param name 지하철 역 이름
     * @return 지하철 역 생성 요청 결과
     */
    protected ExtractableResponse<Response> 지하철_역_생성(String name) {
        RequestBuilder params = new RequestBuilder()
                .add("name", name);

        return RestAssured
                .given().log().all()
                .body(params.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 역 조회 요청합니다
     * @return 지하철 역 조회 요청 결과
     */
    protected ExtractableResponse<Response> 지하철역_목록_조회() {

        return RestAssured
                .given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    /**
     * 존재하는 지하철역 중 name 을 가진 역이 있는지 확인합니다.
     * @param names 확인할 지하철 노선 이름들
     */
    protected void 지하철역_목록_포함_여부_확인(String... names) {

        ExtractableResponse<Response> response = 지하철역_목록_조회();

        지하철역_목록_포함_여부_확인(response, names);
    }

    /**
     * 지하철 역 목록 조회 응답 결과 중 지하철 역 중 names 을 가진 역이 있는지 확인합니다.
     * @param response 지하철 역 목록 조회 응답 결과
     * @param names 확인할 지하철 역 이름들
     */
    protected void 지하철역_목록_포함_여부_확인(ExtractableResponse<Response> response, String... names) {
        List<String> nameResponses = response.jsonPath().getList("name", String.class);
        Assertions.assertThat(nameResponses).containsExactly(names);
    }

    /**
     * name 을 가진 지하철역을 비활성화 요청을 합니다
     * @param name 지하철 역 이름
     * @return 지하철역 비활성화 요청 결과
     */
    protected ExtractableResponse<Response> 지하철역_비활성화(String name) {
        long id = 지하철_역_식별자_조회(name);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/{id}", id)
                .then().log().all()
                .extract();
    }

    /**
     * name 을 가진 지하철 역의 식별자를 조회합니다
     * @param name 지하철 역 이름
     * @return 지하철 역 식별자
     */
    public long 지하철_역_식별자_조회(String name) {
        ExtractableResponse<Response> response = 지하철역_목록_조회();

        return response.jsonPath().param("name", name).getLong("find { node -> node.name == name }.id");
    }

    /**
     * 존재하는 지하철역 중 name 을 가진 역이 없는지 확인합니다.
     * @param name 확인할 지하철 노선 이름
     */
    protected void 지하철역_목록_미포함_여부_확인(String name) {

        ExtractableResponse<Response> response = 지하철역_목록_조회();

        List<String> names = response.jsonPath().getList("name", String.class);
        Assertions.assertThat(names).doesNotContain(name);
    }



}