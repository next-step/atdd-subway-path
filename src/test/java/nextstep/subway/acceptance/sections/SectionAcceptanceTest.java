package nextstep.subway.acceptance.sections;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.fixture.LineFixture;
import nextstep.subway.acceptance.fixture.SectionFixture;
import nextstep.subway.acceptance.fixture.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.util.RestAssuredUtil.*;
import static org.assertj.core.api.Assertions.assertThat;


@AcceptanceTest
@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {
    private ExtractableResponse<Response> 신림역;
    private ExtractableResponse<Response> 보라매역;
    private ExtractableResponse<Response> 서원역;
    private ExtractableResponse<Response> 신림선;

    /**
     * given 지하철 노선을 생성하고
     */
    @BeforeEach
    void before() {
        신림역 = 생성_요청(
                StationFixture.createStationParams("신림역"),
                "/stations");
        보라매역 = 생성_요청(
                StationFixture.createStationParams("보라매역"),
                "/stations");
        서원역 = 생성_요청(
                StationFixture.createStationParams("서원역"),
                "/stations");

        신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");
    }

    /**
     * when 노선 마지막에 지하철 역을 추가하면
     * then 지하철 구간이 생성된다.
     */
    @DisplayName("지하철 구간에 마지막 역을 추가 할 수 있다.")
    @Test
    void 지하철_구간_마지막역을_추가한다() {
        //when
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations.id", Long.class))
                .containsExactly(신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"));
    }

    /**
     * when 노선 가운데 지하철 역을 추가하면
     * then 구간이 생성된다.
     */
    @DisplayName("지하철 구간에 가운데 역을 추가 할 수 있다.")
    @Test
    void 지하철_구간_가운데역을_추가한다() {
        //when
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(신림역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 9L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations.id", Long.class))
                .containsExactly(신림역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"));
    }

    /**
     * when 노선 처음 지하철 역을 추가하면
     * then 구간이 생성된다.
     */
    @DisplayName("지하철 구간에 처음 역을 추가 할 수 있다.")
    @Test
    void 지하철_구간_처음역을_추가한다() {
        //when
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(서원역.jsonPath().getLong("id"), 신림역.jsonPath().getLong("id"), 9L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations.id", Long.class))
                .containsExactly(서원역.jsonPath().getLong("id"), 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"));
    }

    /**
     * when 동일한 노선을 등록하면.
     * then 에러가 발생한다.
     */
    @DisplayName("동일한 구간을 등록하면 에러가 발생한다.")
    @Test
    void 동일한구간_에러발생() {
        //when
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(신림역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 20L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * when 노선 길이가 기존 노선 길이를 초과하여 구간을 등록하면.
     * then 에러가 발생한다.
     */
    @DisplayName("노선의 길이가 초과되도록 구간을 등록하면 에러가 발생한다.")
    @Test
    void 노선길이_초과에러() {
        //when
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 지하철 노선에 구간을 추가로 생성하고
     * when 마지막역을 삭제하면
     * then 노선을 조회시 노선의 삭제된 역만 빼고 조회된다.
     */
    @DisplayName("지하철 구간의 마지막역을 삭제한다.")
    @Test
    void 지하철구간_마지막역_삭제() {
        //given
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 서원역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations")).containsExactly(신림역.jsonPath().get(), 보라매역.jsonPath().get());
    }

    /**
     * given 지하철 노선에 구간을 추가로 생성하고
     * when 처음역을 삭제하면
     * then 노선을 조회시 노선의 삭제된 역만 빼고 조회된다.
     */
    @DisplayName("지하철 구간의 처음역을 삭제한다.")
    @Test
    void 지하철구간_처음역_삭제() {
        //given
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 신림역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations")).containsExactly(보라매역.jsonPath().get(), 서원역.jsonPath().get());
    }

    /**
     * given 지하철 노선에 구간을 추가로 생성하고
     * when 중간역을 삭제하면
     * then 노선을 조회시 노선의 삭제된 역만 빼고 조회된다.
     */
    @DisplayName("지하철 구간의 중간역을 삭제한다.")
    @Test
    void 지하철구간_중간역_삭제() {
        //given
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 보라매역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations")).containsExactly(신림역.jsonPath().get(), 서원역.jsonPath().get());
    }

    /**
     * when 구간이 하나만 있는 노선의 역을 제거하면
     * then 오류가 난다.
     */
    @DisplayName("구간이 하나 있는 역을 삭제하면 오류가 발생한다.")
    @Test
    void 구간이하나만있는역을삭제하면오류() {
        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 보라매역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
