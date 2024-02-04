package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.request.LineRequest;
import nextstep.subway.utils.StationTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.LineTestUtil.createSubwayLine;
import static nextstep.subway.utils.LineTestUtil.getLines;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/truncate.sql")
public class LineAcceptanceTest {

    long stationId1, stationId2, stationId3;
    @BeforeEach
    void setUp() {
        stationId1 = StationTestUtil.createStation("지하철역").jsonPath().getLong("id");
        stationId2 = StationTestUtil.createStation("새로운지하철역").jsonPath().getLong("id");
        stationId3 = StationTestUtil.createStation("또다른지하철역").jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createSubwayLineTest() {
        // when
        ExtractableResponse<Response> response = createSubwayLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> subwayLines = getLines();
        assertThat(subwayLines).containsAnyOf("신분당선");
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void showSubwayLines() {
        //given
        createSubwayLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10));
        createSubwayLine(new LineRequest("분당선", "bg-green-600", stationId1, stationId3, 10));

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        //then
        List<String> subwayLineNames = response.jsonPath().getList("name", String.class);
        //then
        assertThat(subwayLineNames).containsAnyOf("신분당선", "분당선");
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void showSubwayLine() {
        //given
        long id = createSubwayLine(new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 10)).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        //then
        String name = response.jsonPath().get("name");
        assertThat(name).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateSubwayLine() {
        //given
        long id = createSubwayLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10)).jsonPath().getLong("id");

        //when
        Map<String, String> params = new HashMap<>();
        params.put("name", "다른 분당선");
        params.put("color", "bg-red-600");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        //then
        List<String> subwayLineNames = getLines();
        assertThat(subwayLineNames.size()).isEqualTo(1);
        assertThat(subwayLineNames).containsAnyOf("다른 분당선");
    }


    /**
     * Given 지하철 노선을 생성고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteSubwayLine() {
        //given
        long id = createSubwayLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10)).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        //then
        List<String> stationNames = getLines();
        assertThat(stationNames).doesNotContain("신분당선");
    }

}