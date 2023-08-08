package subway.acceptance.apiTester;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import subway.application.response.StationResponse;
import subway.application.response.SubwayLineResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public abstract class SubwayLineAcceptanceTest extends StationAcceptanceTest{

    /**
     * 지하철 노선 생성 요청을 합니다
     * @param name 지하철 노선 이름
     * @param color 지하철 노선 색상 (bg-blue-600, bg-green-600, bg-yellow-600, bg-brown-600, bg-purple-600, bg-gray-600)
     * @param upStationName 상행 종점역 이름
     * @param downStationName 하행 종점역 이름
     * @param distance 거리
     * @return 지하철 노선 생성 요청 결과
     */
    protected ExtractableResponse<Response> 지하철_노선_생성(String name, String color, String upStationName, String downStationName, int distance) {
        super.지하철_역_생성(upStationName);
        super.지하철_역_생성(downStationName);

        long upStationId = super.지하철_역_식별자_조회(upStationName);
        long downStationId = super.지하철_역_식별자_조회(downStationName);

        RequestBuilder params = new RequestBuilder()
                .add("name", name)
                .add("color", color)
                .add("upStationId", upStationId)
                .add("downStationId", downStationId)
                .add("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/subway-lines")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 지하철_노선_생성(String name, String color, StationResponse upStation, StationResponse downStation, int distance) {

        long upStationId = upStation.getId();
        long downStationId = downStation.getId();

        RequestBuilder params = new RequestBuilder()
                .add("name", name)
                .add("color", color)
                .add("upStationId", upStationId)
                .add("downStationId", downStationId)
                .add("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/subway-lines")
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 조회 요청합니다
     * @return 지하철 노선 조회 요청 결과
     */
    protected ExtractableResponse<Response> 지하철_노선_목록_조회() {

        return RestAssured
                .given().log().all()
                .when().get("/subway-lines")
                .then().log().all()
                .extract();
    }

    /**
     * name을 가진 지하철 노선의 상세 정보를 조회합니다.
     * @param name 조회할 지하철 노선 이름
     * @return 지하철 노선 상세 정보 조회 요청 결과
     */
    protected ExtractableResponse<Response> 지하철_노선_상세_조회(String name) {

        long id = 지하철_노선_식별자_검색(name);

        return RestAssured
                .given().log().all()
                .when().get("/subway-lines/{subway-line-id}", id)
                .then().log().all()
                .extract();
    }

    protected void 지하철_노선_구간_확인(SubwayLineResponse subwayLine, StationResponse... stations) {

        SubwayLineResponse subwayLineResponse = RestAssured
                .given().log().all()
                .when().get("/subway-lines/{subway-line-id}", subwayLine.getId())
                .then().log().all()
                .extract().as(SubwayLineResponse.class);

        Assertions.assertThat(subwayLineResponse.getStations())
                .extracting("id")
                .containsExactly(Arrays.stream(stations).map(StationResponse::getId).toArray(Long[]::new));

    }

    protected void 지하철_노선_구간_미포함_확인(SubwayLineResponse subwayLine, StationResponse... stations) {
        String[] stationNames = Arrays.stream(stations).map(StationResponse::getName).toArray(String[]::new);

        SubwayLineResponse subwayLineResponse = RestAssured
                .given().log().all()
                .when().get("/subway-lines/{subway-line-id}", subwayLine.getId())
                .then().log().all()
                .extract().as(SubwayLineResponse.class);

        Assertions.assertThat(subwayLineResponse.getStations())
                .extracting(SubwayLineResponse.StationInfo::getName)
                .doesNotContain(stationNames);
    }

    protected void 지하철_노선_상세_조회_응답_비교(ExtractableResponse<Response> response, String name, String color, String upStationName, String downStationName) {
        SubwayLineResponse subwayLineResponse = response.body().as(SubwayLineResponse.class);

        assertAll(
                () -> assertThat(subwayLineResponse.getName()).isEqualTo(name),
                () -> assertThat(subwayLineResponse.getColor()).isEqualTo(color),
                () -> assertThat(subwayLineResponse.getStations())
                        .extracting(SubwayLineResponse.StationInfo::getName)
                        .containsOnly(upStationName, downStationName));

    }

    /**
     * 존재하는 지하철 노선 중 name 을 가진 노선이 있는지 확인하고 식별자를 리턴합니다.
     * @param name 확인할 지하철 노선 이름
     * @return 지하철 노선 식별자
     */
    protected long 지하철_노선_식별자_검색(String name) {
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        return response.jsonPath().param("name", name).getLong("find { node -> node.name == name }.id");
    }

    /**
     * 지하철 노선 수정 요청을 합니다.
     * @param currentName 현재 지하철 노선 이름
     * @param newName 새로운 지하철 노선 이름
     * @param color 새로운 지하철 노선 색상 (bg-blue-600, bg-green-600, bg-yellow-600, bg-brown-600, bg-purple-600, bg-gray-600)
     * @return 지하철 노선 수정 요청 결과
     */
    protected ExtractableResponse<Response> 지하철_노선_수정(String currentName, String newName, String color) {
        long id = 지하철_노선_식별자_검색(currentName);

        RequestBuilder params = new RequestBuilder()
                .add("name", newName)
                .add("color", color);

        return RestAssured
                .given().log().all()
                .body(params.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/subway-lines/{subway-line-id}", id)
                .then().log().all()
                .extract();
    }

    /**
     * 지하철 노선 비활성화 요청을 합니다.
     * @param name 비활성화할 지하철 노선 이름
     * @return 지하철 노선 비활성화 요청 결과
     */
    protected ExtractableResponse<Response> 지하철_노선_비활성화(String name) {
        long id = 지하철_노선_식별자_검색(name);

        return RestAssured
                .given().log().all()
                .when().delete("/subway-lines/{subway-line-id}", id)
                .then().log().all()
                .extract();
    }

    /**
     * 존재하는 지하철 노선 중 name 을 가진 노선이 있는지 확인합니다.
     * @param names 확인할 지하철 노선 이름 배열
     */
    protected void 지하철_노선_목록_포함_여부_확인(String... names) {

        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        지하철_노선_목록_포함_여부_확인(response, names);
    }

    /**
     * 지하철 역 목록 조회 응답 결과 중 지하철 역 중 names 을 가진 역이 있는지 확인합니다.
     * @param response 지하철 역 목록 조회 응답 결과
     * @param names 확인할 지하철 역 이름들
     */
    protected void 지하철_노선_목록_포함_여부_확인(ExtractableResponse<Response> response, String... names) {
        List<String> nameResponses = response.jsonPath().getList("name", String.class);
        Assertions.assertThat(nameResponses).containsExactly(names);
    }

}
