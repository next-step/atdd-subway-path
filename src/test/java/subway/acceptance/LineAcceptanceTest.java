package subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.acceptance.steps.LineSteps.지하철역_생성;
import static subway.acceptance.steps.LineSteps.노선_삭제;
import static subway.acceptance.steps.LineSteps.노선_생성;
import static subway.acceptance.steps.LineSteps.노선_수정;
import static subway.acceptance.steps.LineSteps.노선_조회;
import static subway.factory.SubwayNameFactory.수인분당선;
import static subway.factory.SubwayNameFactory.신분당선;
import static subway.factory.SubwayNameFactory.우이신설선;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import subway.acceptance.factory.LineRequestFactory;
import subway.dto.LineRequest;

@DisplayName("노선 관련 기능")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        지하철역_생성(); // 노선 생성에 필요한 지하철역 추가
    }

    private static final String linePath = "/lines";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("[성공] 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        노선_생성(LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 조회한_노선 = 노선_조회().extract();

        assertThat(조회한_노선.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> lineNames = 조회한_노선.jsonPath().getList("name", String.class);
        assertThat(lineNames.size()).isEqualTo(1);
        assertThat(lineNames).contains(신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("[성공] 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        노선_생성(LineRequestFactory.create(신분당선)).statusCode(HttpStatus.CREATED.value());
        노선_생성(LineRequestFactory.create(우이신설선)).statusCode(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> 조회한_노선 = 노선_조회().extract();

        // then
        assertThat(조회한_노선.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> lineNames = 조회한_노선.jsonPath().getList("name", String.class);
        assertThat(lineNames.size()).isEqualTo(2);
        assertThat(lineNames).contains(신분당선, 우이신설선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("[성공] 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        노선_생성(LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value())
            .extract();

        // when
        ExtractableResponse<Response> 조회한_노선 = 노선_조회().extract();

        // then
        List<String> lineNames = 조회한_노선.jsonPath().getList("name", String.class);
        assertThat(lineNames.size()).isEqualTo(1);
        assertThat(lineNames).contains(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("[성공] 지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        노선_생성(LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value())
            .extract();
        // when

        LineRequest 노선_수정_사항 = LineRequest.builder()
            .name("수인분당선")
            .color("bg-yellow-600")
            .build();
        String updatedLineName = 수인분당선;
        String updatedLineColor = "bg-yellow-600";
        ExtractableResponse<Response> 수정한_노선 = 노선_수정(1L, 노선_수정_사항).extract();

        // then
        assertThat(수정한_노선.statusCode()).isEqualTo(HttpStatus.OK.value());

        JsonPath responseJsonPath = 수정한_노선.jsonPath();
        assertThat((String) responseJsonPath.get("name")).isEqualTo(updatedLineName);
        assertThat((String) responseJsonPath.get("color")).isEqualTo(updatedLineColor);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("[성공] 지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        노선_생성(LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value())
            .extract();

        // when
        ExtractableResponse<Response> response = 노선_삭제(1L).extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
