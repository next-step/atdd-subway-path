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
    private static ExtractableResponse<Response> 신림역;
    private static ExtractableResponse<Response> 보라매역;
    private static ExtractableResponse<Response> 대방역;
    private static ExtractableResponse<Response> 서원역;
    private static ExtractableResponse<Response> 판교역;
    private static ExtractableResponse<Response> 청계산입구역;
    private static ExtractableResponse<Response> 신분당선;

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
    }

    /**
     * given 지하철 노선을 생성하고
     * when 지하철 구간을 생성하면
     * then 지하철 구간이 생성된다.
     */
    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createLineSection() {
        //given
        ExtractableResponse<Response> 신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");

        //when
        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성.statusCode()).isEqualTo(201);
        assertThat(신림선_구간_생성.jsonPath().getString("upStations.name")).isEqualTo(보라매역.jsonPath().getString("name"));
        assertThat(신림선_구간_생성.jsonPath().getString("downStations.name")).isEqualTo(서원역.jsonPath().getString("name"));
    }


    /**
     * given 지하철 노선을 생성하고
     * when 등록된 역을 하행성으로 구간을 등록하면
     * then 오류가 난다.
     */
    @DisplayName("이미 해당노선에 등록되어있는 역은 새로운 구간의 하행역이면 오류가 반환된다.")
    @Test
    void createLineSection_checkDuplicateStation() {
        //given
        ExtractableResponse<Response> 신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");

        //when
        ExtractableResponse<Response> 신림선_구간_생성_오류 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 신림역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성_오류.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 지하철 노선을 생성하고
     * when 하행종점역이 아닌 역을 구간으로 등록하면
     * then 오류가 난다.
     */
    @DisplayName("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이 아니면 오류가 반환된다.")
    @Test
    void createLineSection_invalidUpStation() {
        //given
        ExtractableResponse<Response> 신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");

        //when
        ExtractableResponse<Response> 신림선_구간_생성_오류 = 생성_요청(
                SectionFixture.createSectionParams(신림역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //then
        assertThat(신림선_구간_생성_오류.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 지하철 노선에 구간을 두개 생성한다.
     * when 한개의 구간을 삭제하면
     * then 노선을 조회시 노선의 삭제된 역만 빼고 조회된다.
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void removeLineSection() {
        //given
        ExtractableResponse<Response> 신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");

        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 서원역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> 신림선_조회 = 조회_요청("/lines/" + 신림선.jsonPath().getLong("id"));
        assertThat(신림선_조회.jsonPath().getList("stations").size()).isEqualTo(2);
    }

    /**
     * given 지하철 노선에 구간을 두개 생성한다.
     * when 하행 종점역이 아닌 역을 제거하면
     * then 오류가 난다.
     *
     */
    @DisplayName("삭제 역이 하행 종점역이 아니면 오류가 반환된다.")
    @Test
    void removeLineSection_InvalidDownStationException() {
        //given
        ExtractableResponse<Response> 신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");

        ExtractableResponse<Response> 신림선_구간_생성 = 생성_요청(
                SectionFixture.createSectionParams(보라매역.jsonPath().getLong("id"), 서원역.jsonPath().getLong("id"), 10L),
                "/lines/" + 신림선.jsonPath().getLong("id") + "/sections"
        );

        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 보라매역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * given 지하철 노선에 구간을 생성한다.
     * when 구간이 하나만 있는 노선의 역을 제거하면
     * then 오류가 난다.
     *
     */
    @DisplayName("삭제 역이 하행 종점역이 아니면 오류가 반환된다.")
    @Test
    void removeLineSection_SingleSectionDeleteException() {
        //given
        ExtractableResponse<Response> 신림선 = 생성_요청(
                LineFixture.createLineParams("신림선", "BLUE", 신림역.jsonPath().getLong("id"), 보라매역.jsonPath().getLong("id"), 20L),
                "/lines");

        //when
        ExtractableResponse<Response> 삭제_결과 = 삭제_요청("/lines/" + 신림선.jsonPath().getLong("id") + "/sections?stationId=" + 보라매역.jsonPath().getLong("id"));

        //then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
