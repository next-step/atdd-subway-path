package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "이름");
        map.put("color", "컬러");

        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all().body(map)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 지하철 노선을 생성한다.
     * Given 새로운 지하철 노선을 생성한다.
     * When 지하철 노선 조회를 요청한다.
     * Then 지하철 노선을 반환한다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {

        //given

        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = new HashMap<>();
        param.put("name", 기존노선);
        param.put("color", 기존색상);

        String 새로운노선 = "새로운 노선";
        String 새로운색상 = "새로운 색상";
        Map<String, String> param2 = new HashMap<>();
        param2.put("name", 새로운노선);
        param2.put("color", 새로운색상);

        //when
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().post("/lines")
                .then();


        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param2)
                .when().post("/lines")
                .then();


        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();

        //then
        assertThat(response.jsonPath().getList("name")).contains(새로운노선, 기존노선);
        assertThat(response.jsonPath().getList("color")).contains(새로운색상, 새로운색상);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성된 지하철 노선을 요청한다.
     * Then 생성된 지하철 노선을 반환한다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {

        //given

        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = new HashMap<>();
        param.put("name", 기존노선);
        param.put("color", 기존색상);

        //when
        ExtractableResponse<Response> createResponse = RestAssured
                .given()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().extract();

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(기존노선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선 수정을 요청한다,
     * Then 지하철 노선이 수정이 완료된다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        //given

        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = new HashMap<>();
        param.put("name", 기존노선);
        param.put("color", 기존색상);

        String 수정노선 = "수정 노선";
        String 수정색상 = "수정 색상";
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", 수정노선);
        updateParam.put("color", 수정색상);

        //when
        RestAssured
                .given()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().extract();

        ExtractableResponse<Response> updateResponse = RestAssured
                .given()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/1")
                .then().extract();

        ExtractableResponse<Response> response = RestAssured
                .given()
                .when().get("/lines/1")
                .then().extract();

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(수정노선);


    }

    /**
     * Given 지하철 역 생성을 요청한다.
     * When 지하철 역 삭제를 요청한다.
     * Then 지하철 역이 삭제된다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        //given
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = new HashMap<>();
        param.put("name", 기존노선);
        param.put("color", 기존색상);

        //when
        RestAssured
                .given()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then();

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/1")
                .then().log().all().extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
