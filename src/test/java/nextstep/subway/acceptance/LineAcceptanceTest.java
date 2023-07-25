package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.response.LineResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_생성;
import static nextstep.subway.acceptance.steps.LineSteps.지하철_노선_조회;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_응답;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final String COLOR_RED = "bg-red-600";

    private final String COLOR_BLUE = "bg-blue-600";
    private final int DISTANCE = 10;

    private StationResponse 신사역, 광교역, 소요산역, 광명역;
    @BeforeEach
    void set(){
        신사역 = 지하철역_생성_응답("신사역");
        광교역 = 지하철역_생성_응답("광교역");
        소요산역 = 지하철역_생성_응답("소요산역");
        광명역 = 지하철역_생성_응답("광명역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * */
    @DisplayName("노선 생성")
    @Test
    void createLineTest() {
        //given
        //when
        ExtractableResponse<Response> response =
                지하철_노선_생성("신분당선", COLOR_RED, 신사역.getId(), 광교역.getId(), DISTANCE);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse line = 지하철_노선_조회().jsonPath().getList("",LineResponse.class).get(0);

        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo(COLOR_RED);
    }



    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     * */
    @DisplayName("노선 목록 조회")
    @Test
    void showLines() {
        //given
        지하철_노선_생성("신분당선", COLOR_RED, 신사역.getId(), 광교역.getId(), DISTANCE);
        지하철_노선_생성("1호선", COLOR_BLUE,  소요산역.getId(), 광명역.getId(), DISTANCE);

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회();
        List<String> lines = response.jsonPath().getList("name", String.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).contains("신분당선","1호선");

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * */
    @DisplayName("노선 정보 조회")
    @Test
    void showLineInfo() {
        //given
        지하철_노선_생성("신분당선",COLOR_RED, 신사역.getId(), 광교역.getId() , DISTANCE);

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회();
        LineResponse line = response.jsonPath().getList("", LineResponse.class).get(0);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(line.getName()).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * */
    @DisplayName("노선 정보 수정")
    @Test
    void updateLineInfo() {
        //given
        Long 신분당선 = 지하철_노선_생성("신분당선", COLOR_RED, 신사역.getId(), 광교역.getId(),DISTANCE)
                .jsonPath().getObject("", LineResponse.class).getId();

        //when
        Map<String, Object> param = new HashMap<>();
        param.put("id",신분당선);
        param.put("name","1호선");
        param.put("color",COLOR_BLUE);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines")
                .then().log().all()
                .extract();

        LineResponse line = response.jsonPath().getObject("", LineResponse.class);


        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getColor()).isEqualTo(COLOR_BLUE);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * */
    @DisplayName("노선 삭제")
    @Test
    void deleteLine() {

        //given
        Long 신분당선 = 지하철_노선_생성("신분당선", COLOR_RED
                , 신사역.getId()
                , 광교역.getId()
                ,10)
                .jsonPath().getObject("", LineResponse.class).getId();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/lines/" + 신분당선)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());


        List<String> names = 지하철_노선_조회().jsonPath().getList("name", String.class);

        assertThat(names).isEmpty();
    }

}
