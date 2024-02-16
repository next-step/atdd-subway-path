package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.presentation.request.LineCreateRequest;
import nextstep.subway.line.presentation.request.LineUpdateRequest;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static io.restassured.path.json.JsonPath.from;
import static nextstep.subway.steps.LineSteps.*;
import static nextstep.subway.steps.StationSteps.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        databaseCleanup.execute();
    }

    /**
     *  When : 지하철 노선을 생성하고
     *  Then : 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철노선을_생성하고_조회한다() {
        // given
        Long 건대입구_id = createStation("건대입구");
        Long 어린이대공원_id = createStation("어린이대공원");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                건대입구_id,
                어린이대공원_id,
                10
        );

        // when
        ExtractableResponse<Response> response = createLine(lineCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf(
                "건대입구", "어린이대공원"
        );
    }

    /**
     *  Given 2개의 지하철 노선을 생성하고
     *  When 지하철 노선 목록을 조회하면
     *  Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 생성한_지하철노선을_모두_조회한다() {
        // given
        Long 건대입구_id = createStation("건대입구");
        Long 어린이대공원_id = createStation("어린이대공원");
        Long 군자_id = createStation("군자");

        LineCreateRequest lineCreateRequest_1 = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                건대입구_id,
                어린이대공원_id,
                10
        );
        createLine(lineCreateRequest_1);

        LineCreateRequest lineCreateRequest_2 = new LineCreateRequest(
                "분당선",
                "bg-green-600",
                건대입구_id,
                군자_id,
                20
        );
        createLine(lineCreateRequest_2);

        // when
        ExtractableResponse<Response> response = getLines();
        String responseJson = response.body().asString();
        List<Map<String, ?>> lines = from(responseJson).getList("");

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(lines.get(0).get("name")).isEqualTo("신분당선");
        assertThat((List<Map<String, ?>>) lines.get(0).get("stations")).extracting("name").containsExactlyInAnyOrder("건대입구", "어린이대공원");

        assertThat(lines.get(1).get("name")).isEqualTo("분당선");
        assertThat((List<Map<String, ?>>) lines.get(1).get("stations")).extracting("name").containsExactlyInAnyOrder("건대입구", "군자");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 한개의_지하철_노선을_조회한다() {
        // given
        Long 건대입구_id = createStation("건대입구");
        Long 어린이대공원_id = createStation("어린이대공원");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                건대입구_id,
                어린이대공원_id,
                10
        );
        ExtractableResponse<Response> extractableResponse = createLine(lineCreateRequest);
        Long lineId = extractableResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = getLine(lineId);

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getList("stations.name", String.class)).containsAnyOf(
                "건대입구", "어린이대공원"
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_정보를_수정한다() {
        // given
        Long 건대입구_id = createStation("건대입구");
        Long 어린이대공원_id = createStation("어린이대공원");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                건대입구_id,
                어린이대공원_id,
                10
        );
        ExtractableResponse<Response> extractableResponse = createLine(lineCreateRequest);
        Long lineId = extractableResponse.jsonPath().getLong("id");

        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(
                "구분당선",
                "blue"
        );

        // when
        ExtractableResponse<Response> response = putLine(lineId, lineUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선을_삭제한다() {
        // given
        Long 건대입구_id = createStation("건대입구");
        Long 어린이대공원_id = createStation("어린이대공원");

        LineCreateRequest lineCreateRequest = new LineCreateRequest(
                "신분당선",
                "bg-red-600",
                건대입구_id,
                어린이대공원_id,
                10
        );
        ExtractableResponse<Response> extractableResponse = createLine(lineCreateRequest);
        Long lineId = extractableResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = deleteLine(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(204);
    }


    private ExtractableResponse<Response> createLine(LineCreateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }


}
