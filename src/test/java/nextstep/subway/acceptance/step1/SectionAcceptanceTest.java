package nextstep.subway.acceptance.step1;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanUp;

@DirtiesContext
@Sql(
    scripts = "/sql/insert-line-and-section.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DisplayName("지하철 구간 추가 기능 개선 - 테스트")
public class SectionAcceptanceTest {

    private final Long 이호선_ID = 1L;
    @Autowired
    private DatabaseCleanUp dataBaseCleanUp;

    @AfterEach
    void setUp() {
        dataBaseCleanUp.excute();
    }

    /**
     * given 노선의 기존 구간(강남역-선릉역)이 존재할 때,
     * when 신규 구간(강남역-역삼역)을 추가하면
     * then 총 2개의 구간(강남역-역삼역, 역삼역-선릉역)이 생긴다.
     */
    @Test
    @DisplayName("기존 구간 사이에 새로운 구간 추가")
    void add_Between_Exist_Section() {

        // given 기존 구간 조회
        int 기존_구간_길이 = 10;
        int 신규_구간_길이 = 7;

        // when 신규 구간 삽입
        구간추가(1L, 2L, 3L, 신규_구간_길이);

        // then 역 조회
        List<String> stations = 역_이름_조회();
        assertThat(stations).containsExactly("강남역", "역삼역", "선릉역");

        // then 구간 조회
        List<String> sections = 구간_상행역_조회();
        assertThat(sections.size()).isEqualTo(2);

        // then 구간 거리 검증
        List<Integer> distances = 구간_거리_조회();
        int 구간_총_길이 = distances.stream().mapToInt(i -> i).sum();
        assertEquals(구간_총_길이, 기존_구간_길이);
    }

    /**
     * given 노선의 기존 구간(강남역-역삼역)이 존재할 때,
     * when 신규 구간(교대역-강남역)을 추가하면
     * then 총 2개의 구간(교대역-강남역, 강남역-역삼역)이 생긴다.
     */
    @Test
    @DisplayName("기존 구간에 추가 : 상행역 기준")
    void add_Before_UpStation() {
        // given sql 대체
        int 기존_구간_길이 = 10;
        int 신규_구간_길이 = 7;

        // when 신규 구간 삽입
        구간추가(1L, 1L, 2L, 신규_구간_길이);

        // then 역 조회
        List<String> stations = 역_이름_조회();
        assertThat(stations).containsExactly("교대역", "강남역", "선릉역");

        // then 구간 조회
        List<String> sections = 구간_상행역_조회();
        assertThat(sections.size()).isEqualTo(2);

        List<Integer> distances = 구간_거리_조회();
        int 구간_총_길이 = distances.stream().mapToInt(i -> i).sum();
        assertEquals(구간_총_길이, 기존_구간_길이 + 신규_구간_길이);
    }

    /**
     * given 노선의 기존 구간(강남역-역삼역)이 존재할 때,
     * when 신규 구간(역삼역-선릉역)을 추가하면
     * then 총 2개의 구간(강남역-역삼역, 역삼역-선릉역)이 생긴다.
     */
    @Test
    @DisplayName("기존 구간에 추가 : 하행역 기준")
    void add_After_DownStation() {
        // given sql 대체
        int 기존_구간_길이 = 10;
        int 신규_구간_길이 = 7;

        // when 신규 구간 삽입
        long 기준_하행역 = 4L;
        구간추가(1L, 기준_하행역, 5L, 신규_구간_길이);

        // then 역 조회
        List<String> stations = 역_이름_조회();
        assertThat(stations).containsExactly("강남역", "선릉역", "삼성역");

        // then 구간 조회
        List<String> sections = 구간_상행역_조회();
        assertThat(sections.size()).isEqualTo(2);

        List<Integer> distances = 구간_거리_조회();
        int 구간_총_길이 = distances.stream().mapToInt(i -> i).sum();
        assertEquals(구간_총_길이, 기존_구간_길이 + 신규_구간_길이);
    }

    /**
     * given 노선의 기존 구간이 존재할 때,
     * when 기존 구간 사이에 신규 구간 추가시, 기존 구간과 신규 구간의 길이가 같다면
     * then 예외를 던진다.
     */
    @Test
    @DisplayName("구간 추가 실패 : 구간 길이 동일할 때")
    void throw_Exception() {
        // given Sql 대체
        int 기존_구간_길이 = 10;

        // when then
        int status = 구간추가(1L, 3L, 4L, 기존_구간_길이).jsonPath().getInt("status");
        assertThat(HttpStatus.INTERNAL_SERVER_ERROR.value()).isEqualTo((status));
    }

    /**
     * given 노선에 두개의 구간이 존재할 때,
     * when 중간 역을 삭제하면
     * then 하나의 구간만 남는다.
     */
    @Test
    @Sql(
        scripts = "/sql/insert-additional-section.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("중간 구간 삭제 테스트")
    void remove_Middle_Section() {
        // given Sql 대체
        int 기존_구간_길이 = 20;
        // when
        구간삭제(이호선_ID, 2L);

        // then
        List<String> stations = 역_이름_조회();
        assertThat(stations).containsExactly("교대역", "역삼역");

        List<Integer> distances = 구간_거리_조회();
        int 구간_총_길이 = distances.stream().mapToInt(i -> i).sum();
        assertEquals(구간_총_길이, 기존_구간_길이);
    }

    /**
     * given 노선에 2개의 구간이 존재할 때,
     * when 종점(상행선 or 하행선)을 삭제하면
     * then 하나의 구간만 남는다.
     */
    @Test
    @Sql(
        scripts = "/sql/insert-additional-section.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("끝 구간 삭제 테스트")
    void remove_End_Section() {
        // given Sql 대체
        int 기존_구간_길이 = 20;
        int 삭제된_구간_길이 = 10;

        // when
        구간삭제(이호선_ID, 1L);

        // then
        List<String> stations = 역_이름_조회();
        assertThat(stations).containsExactly("강남역", "역삼역");

        List<Integer> distances = 구간_거리_조회();
        int 구간_총_길이 = distances.stream().mapToInt(i -> i).sum();
        assertEquals(구간_총_길이, 기존_구간_길이 - 삭제된_구간_길이);
    }

    /**
     * given 노선에 하나의 구간만 있을 때,
     * when 구간을 삭제하면
     * then 예외를 던진다.
     */
    @Test
    @Sql(
        scripts = "/sql/insert-additional-section.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("삭제 예외 케이스 테스트")
    void remove_Exception() {
        // given Sql 대체
        구간삭제(이호선_ID, 1L);

        // when
        int status = 구간삭제(이호선_ID, 2L).jsonPath().getInt("status");

        // then
        assertThat(status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> 구간삭제(Long lineId, Long stationId) {

        Map<String, Object> params = new HashMap<>();
        params.put("stationId", stationId);

        ExtractableResponse<Response> response =
            RestAssured.given()
                // .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .all()
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then()
                .log()
                .all()
                .extract();

        return response;
    }

    private ExtractableResponse<Response> 구간추가(
        Long lineId, Long upStationId, Long downStationId, Integer distance) {

        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", downStationId);
        params.put("upStationId", upStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response =
            RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .all()
                .when()
                .post("/lines/{id}/sections", lineId)
                .then()
                .log()
                .all()
                .extract();

        return response;
    }

    private static ExtractableResponse<Response> 노선조회(Long lineId) {
        return RestAssured.given().log().all()
            .when().get("/lines/{lineId}", lineId)
            .then().log().all().extract();
    }

    private List<String> 역_이름_조회() {
        return 노선조회(이호선_ID).jsonPath().getList("stations.name");
    }

    private List<String> 구간_상행역_조회() {
        return 노선조회(이호선_ID).jsonPath().getList("sections.upStation");
    }

    private List<Integer> 구간_거리_조회() {
        return 노선조회(이호선_ID).jsonPath().getList("sections.distance");
    }
}


