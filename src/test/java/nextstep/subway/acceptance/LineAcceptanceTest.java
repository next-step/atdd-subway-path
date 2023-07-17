package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import nextstep.subway.acceptance.commonStep.AcceptanceTest;
import nextstep.subway.acceptance.commonStep.LineStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        // when
        LineStep.지하철_노선_생성( "2호선", "green");

        //then
        List<String> lineNames = LineStep.지하철노선_목록_전체조회();
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하노선 목록을 조회한다.")
    @Test
    void createLines() {
        //given
        LineStep.지하철_노선_생성( "2호선", "green");
        LineStep.지하철_노선_생성( "9호선", "brown");

        //when
        List<String> lineNames = LineStep.지하철노선_목록_전체조회();

        //then
        assertThat(lineNames).hasSize(2);
    }

    /**
     * Given지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine(){
        //given
        Long lineId =  LineStep.지하철_노선_생성( "2호선", "green").jsonPath().getLong("id");

        //when
        JsonPath jsonPath = LineStep.지하철_노선_조회(lineId);

        //then
        assertThat("2호선").isEqualTo(jsonPath.getString("name"));
        assertThat("green").isEqualTo(jsonPath.getString("color"));

    }



    /**
     * Given지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */

    @DisplayName("지하노선을 수정한다.")
    @Test
    void updateLine(){
        //given
        Long lineId =  LineStep.지하철_노선_생성( "2호선", "green").jsonPath().getLong("id");

        //when
        Map<String, String> params = new HashMap<>();
        params.put("name", "9호선");
        params.put("color", "brown");
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put("/lines/"+lineId)
                        .then().log().all()
                        .extract();

        //then
        JsonPath jsonPath = LineStep.지하철_노선_조회(lineId);
        assertThat("9호선").isEqualTo(jsonPath.getString("name"));
        assertThat("brown").isEqualTo(jsonPath.getString("color"));
    }

    /**
     * Given지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하노선을 삭제한다.")
    @Test
    void deleteLine(){
        //given
        Long lineId = LineStep.지하철_노선_생성( "2호선", "green").jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .when().delete("/lines/" + lineId)
                        .then().log().all().extract();

        //then
        assertThat(response.statusCode()).isEqualTo(200);

        List<String> lineNames = LineStep.지하철노선_목록_전체조회();
        assertThat(lineNames).doesNotContain("2호선");

    }
}
