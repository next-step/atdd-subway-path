package nextstep.subway.acceptance;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.section.SectionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static nextstep.subway.utils.AcceptanceMethods.*;
import static nextstep.subway.utils.StationFixtures.stationFixtures;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/table_truncate.sql")
@DisplayName("지하철 구간 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
    /**
     * given 1개의 지하철 노선을 등록하고
     * when 1개의 지하철 구간을 추가 등록하면
     * then 해당 지하철 노선의 구간 조회 시 생성한 구간을 찾을 수 있다.
     */
    @DisplayName("노선 구간 등록")
    @Test
    void createSection() {
        // given
        List<Long> stationIds = stationFixtures(3);

        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = makeSection(lineId, new SectionRequest(stationIds.get(1), stationIds.get(2), 13L));

        // then
        assertThat(response.jsonPath().getLong("upStation.id")).isEqualTo(stationIds.get(1));
        assertThat(response.jsonPath().getLong("downStation.id")).isEqualTo(stationIds.get(2));
    }

    /**
     * given 지하철 노선을 생성 후 1개의 구간을 더 등록하고
     * when 이미 등록된 구간을 등록하면
     * then 구간 등록 에러가 발생한다.
     */
    @DisplayName("에러_지하철 노선 등록_이미 존재하는 역")
    @Test
    void addSectionError_duplicated() {
        // given
        List<Long> stationIds = stationFixtures(3);

        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)).jsonPath().getLong("id");
        makeSection(lineId, new SectionRequest(stationIds.get(1), stationIds.get(2), 13L));
        // when
        // then
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationIds.get(2), stationIds.get(1), 16L))
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * given 지하철 노선을 생성 후
     * when 하행 종점역이 아닌 역을 상행선으로 등록하려고 하면
     * then 구간 등록 에러가 발생한다.
     */
    @DisplayName("에러_지하철 노선 등록_하행 종점역이 일치하지 않음")
    @Test
    void addSectionError_isNotCurrentDownStation() {
        // given
        List<Long> stationIds = stationFixtures(3);
        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)).jsonPath().getLong("id");

        // when
        // then
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationIds.get(0), stationIds.get(1), 13L))
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 2개의 지하철 구간을 등록하고
     * when 1개의 구간을 삭제하면
     * then 지하철 노선 조회 시 1개의 구간만 노출된다.
     */
    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteSection() {
        // given
        List<Long> stationIds = stationFixtures(3);

        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)).jsonPath().getLong("id");
        makeSection(lineId, new SectionRequest(stationIds.get(1), stationIds.get(2), 13L));

        // when
        removeSection(stationIds.get(2), lineId);

        // then
        ExtractableResponse<Response> response = getLineSections(lineId);

        assertThat(response.jsonPath().getList("sections.downStation.id", Long.class)).doesNotContain(stationIds.get(2));
    }


    /**
     * 2개의 지하철 구간을 등록하고
     * 하행 종점역이 아닌 역을 제거하면
     * 구간 제거 에러가 발생한다.
     */
    @DisplayName("에러_지하철 노선 제거_하행 종점역이 아닌 역 제거")
    @Test
    void deleteSectionError_isNotCurrentDownStation() {
        // given
        List<Long> stationIds = stationFixtures(3);
        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)).jsonPath().getLong("id");
        makeSection(lineId, new SectionRequest(stationIds.get(1), stationIds.get(2), 13L));

        // when
        // then
        RestAssured
                .given()
                .param("stationId", stationIds.get(1))
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * given 1개의 지하철 구간을 등록하고
     * when 하행 종점역인 구간을 제거하면
     * then 구간 제거 에러가 발생한다.
     */
    @DisplayName("에러_지하철 노선 제거_상행 종점역과 하행 종점역만 존재")
    @Test
    void deleteSectionError_justOneSection() {
        // given
        List<Long> stationIds = stationFixtures(2);
        Long lineId = makeLine(new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10L)).jsonPath().getLong("id");

        // when
        // then
        RestAssured
                .given()
                .param("stationId", stationIds.get(1))
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
