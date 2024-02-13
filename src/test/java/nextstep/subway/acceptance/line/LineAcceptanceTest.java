package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.fixture.LineFixture;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.acceptance.util.RestAssuredUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
@DisplayName("지하철역 노선 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {
    private static ExtractableResponse<Response> 신림역;
    private static ExtractableResponse<Response> 보라매역;
    private static ExtractableResponse<Response> 판교역;
    private static ExtractableResponse<Response> 청계산입구역;

    private static String 신림선 = "신림선";
    private static String BLUE = "BLUE";
    private static String 신분당선 = "신분당선";
    private static String RED = "RED";

    @BeforeEach
    void before() {
        신림역 = RestAssuredUtil.생성_요청(
                StationFixture.createStationParams("신림역"),
                "/stations");
        보라매역 = RestAssuredUtil.생성_요청(
                StationFixture.createStationParams("보라매역"),
                "/stations");
        판교역 = RestAssuredUtil.생성_요청(
                StationFixture.createStationParams("판교역"),
                "/stations");
        청계산입구역 = RestAssuredUtil.생성_요청(
                StationFixture.createStationParams("청계산입구역"),
                "/stations");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        RestAssuredUtil.생성_요청(
                LineFixture.createLineParams(신림선, BLUE, 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L), "/lines");

        //then
        ExtractableResponse<Response> findResponse = RestAssuredUtil.조회_요청("/lines");

        assertThat(findResponse.jsonPath().getString("name")).containsAnyOf(신림선);
        assertThat(findResponse.jsonPath().getString("color")).containsAnyOf(BLUE);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLine() {
        //given
        Long 신림선_고유번호 = 신림역.jsonPath().getLong("id");
        Long 보라매역_고유번호 = 보라매역.jsonPath().getLong("id");
        Long 신림선_길이 = 10L;
        RestAssuredUtil.생성_요청(
                LineFixture.createLineParams(신림선, BLUE, 신림선_고유번호, 보라매역_고유번호, 신림선_길이), "/lines");

        Long 판교역_고유번호 = 판교역.jsonPath().getLong("id");
        Long 청계산입구역_고유번호 = 청계산입구역.jsonPath().getLong("id");
        Long 신분당선_길이 = 10L;
        RestAssuredUtil.생성_요청(
                LineFixture.createLineParams(신분당선, RED, 판교역_고유번호, 청계산입구역_고유번호, 신분당선_길이), "/lines");

        //when
        ExtractableResponse<Response> lineList = RestAssuredUtil.조회_요청("/lines");

        //then
        assertThat(lineList.jsonPath().getString("name")).containsAnyOf(신림선, 신분당선);
        assertThat(lineList.jsonPath().getString("color")).containsAnyOf(BLUE, RED);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findLine() {
        //given
        Long 신림선_고유번호 = 신림역.jsonPath().getLong("id");
        Long 보라매역_고유번호 = 보라매역.jsonPath().getLong("id");
        Long distance = 10L;
        ExtractableResponse<Response> createResponse = RestAssuredUtil.생성_요청(
                LineFixture.createLineParams(신림선, BLUE, 신림선_고유번호, 보라매역_고유번호, distance), "/lines");

        //when
        ExtractableResponse<Response> findResponse = RestAssuredUtil.조회_요청("/lines/" + createResponse.jsonPath().getLong("id"));

        //then
        assertThat(findResponse.jsonPath().getString("color")).isEqualTo(BLUE);
        assertThat(findResponse.jsonPath().getString("name")).isEqualTo(신림선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        ExtractableResponse<Response> createResponse = RestAssuredUtil.생성_요청(
                LineFixture.createLineParams(신림선, BLUE, 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L), "/lines");

        //when
        String 경강선 = "경강선";
        String YELLOW = "YELLOW";
        RestAssuredUtil.수정_요청(LineFixture.updateLineParams(경강선, YELLOW), "/lines/" + createResponse.jsonPath().getLong("id"));

        //then
        ExtractableResponse<Response> findResponse = RestAssuredUtil.조회_요청("/lines/" + createResponse.jsonPath().getLong("id"));
        assertThat(findResponse.jsonPath().getString("name")).isEqualTo(경강선);
        assertThat(findResponse.jsonPath().getString("color")).isEqualTo(YELLOW);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        ExtractableResponse<Response> createResponse = RestAssuredUtil.생성_요청(
                LineFixture.createLineParams(신림선, BLUE, 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L), "/lines");

        //when
        ExtractableResponse<Response> deleteResponse = RestAssuredUtil.삭제_요청("/lines/" + createResponse.jsonPath().getLong("id"));

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }
}
