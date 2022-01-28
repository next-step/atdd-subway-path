package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.commons.AcceptanceTest;
import nextstep.subway.commons.DatabaseCleanup;
import nextstep.subway.line.acceptance.dto.LineRequest;
import nextstep.subway.line.acceptance.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static nextstep.subway.commons.AssertionsUtils.삭제요청_성공;
import static nextstep.subway.commons.AssertionsUtils.요청_실패;
import static nextstep.subway.line.acceptance.utils.LineUtils.지하철노선_단건조회_요청;
import static nextstep.subway.line.acceptance.utils.LineUtils.지하철노선_생성_요청;
import static nextstep.subway.line.acceptance.utils.SectionUtils.*;
import static nextstep.subway.station.acceptance.StationUtils.지하철역_생성요청;

@DisplayName("지하철 노선의 구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    long 이호선;
    long 이호선_상행역;
    long 이호선_하행역;

    @BeforeEach
    @Override
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();

        LineRequest request = LineRequest.builder()
                 .lineName("2호선")
                 .lineColor("bg-green")
                 .upStationName("신도림역")
                 .downStationName("문래역")
                 .distance(7)
                 .build();

        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        이호선 = response.jsonPath().getLong("id");
        List<Map<String, Object>> 이호선_역목록 = response.jsonPath().getList("stations");

        이호선_상행역 = Long.valueOf(String.valueOf(이호선_역목록.get(0).get("id")));
        이호선_하행역 = Long.valueOf(String.valueOf(이호선_역목록.get(1).get("id")));
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 구간의 상행역이 현재 등록되어있는 하행 종점역이 아니고
     * When 새로운 구간의 생성을 요청하면
     * Then 새로운 구간 생성이 실패한다.
     */
    @DisplayName("새로운 구간의 상행역은 현재 등록되어있는 하행 종점역이어야 한다.")
    @Test
    void validateSectionUpStation() {
        // given
        long 당산역 = 지하철역_생성요청("당산역").jsonPath().getLong("id");
        long 영등포구청역 = 지하철역_생성요청("영등포구청역").jsonPath().getLong("id");

        SectionRequest request = SectionRequest.builder()
                .upStationId(당산역)
                .downStationId(영등포구청역)
                .distance(5)
                .build();

        // when
        ExtractableResponse<Response> response = 지하철노선_구간생성_요청(이호선, request);

        // then
        요청_실패(response);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 구간의 하행역이 현재 등록되어있는 역 중 하나이고
     * When 새로운 구간의 생성을 요청하면
     * Then 새로운 구간 생성이 실패한다.
     */
    @DisplayName("새로운 구간의 하행역은 현재 등록되어있는 역일 수 없다.")
    @Test
    void validateSectionDownStation() {
        // given
        SectionRequest request = SectionRequest.builder()
                .upStationId(이호선_하행역)
                .downStationId(이호선_상행역)
                .distance(5)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(이호선, request);

        // then
        요청_실패(지하철노선_구간생성_응답);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 새로운 구간의 생성을 요청하면
     * Then 새로운 구간 생성이 성공한다.
     */
    @DisplayName("지하철 노선의 구간 생성")
    @Test
    void createSection() {
        // given
        long 영등포구청역 = 지하철역_생성요청("영등포구청역").jsonPath().getLong("id");

        SectionRequest request = SectionRequest.builder()
                .upStationId(이호선_하행역)
                .downStationId(영등포구청역)
                .distance(5)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(이호선, request);
        ExtractableResponse<Response> 지하철노선_단건조회_응답 = 지하철노선_단건조회_요청(이호선);

        // then
        지하철노선_구간생성_요청_성공(지하철노선_구간생성_응답, 지하철노선_단건조회_응답);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 구간이 1개인 경우 역 삭제 요청을 하면
     * Then 역 삭제 요청이 실패한다.
     */
    @DisplayName("지하철 노선에 구간이 1개인 경우 역 삭제를 할 수 없다")
    @Test
    void validateDeleteSection_OneSection() {
        // when
        ExtractableResponse<Response> 지하철노선_역삭제_응답 = 지하철노선_역삭제_요청(이호선, 이호선_상행역);

        // then
        요청_실패(지하철노선_역삭제_응답);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 지하철 노선 구간 생성을 요청 하고
     * When 지하철 노선의 마지막 역이 아닌 역 삭제 요청을 하면
     * Then 역 삭제 요청이 실패한다.
     */
    // displayName에 대한 의견 구하기
    @DisplayName("지하철 노선에 등록된 마지막 역만 제거할 수 있다")
    @Test
    void validateDeleteSection_NonDownStation() {
        // given
        long 영등포구청역 = 지하철역_생성요청("영등포구청역").jsonPath().getLong("id");

        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(이호선_하행역)
                .downStationId(영등포구청역)
                .build();
        지하철노선_구간생성_요청(이호선, sectionRequest);

        // when
        ExtractableResponse<Response> 지하철노선_역삭제_응답 = 지하철노선_역삭제_요청(이호선, 이호선_상행역);

        // then
        요청_실패(지하철노선_역삭제_응답);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 지하철 노선 구간 생성을 요청 하고
     * When 지하철 노선의 마지막 역 삭제 요청을 하면
     * Then 역 삭제 요청이 성공한다.
     */
    @DisplayName("지하철 노선의 역 삭제 요청")
    @Test
    void deleteSection() {
        // given
        long 영등포구청역 = 지하철역_생성요청("영등포구청역").jsonPath().getLong("id");

        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(이호선_하행역)
                .downStationId(영등포구청역)
                .build();
        지하철노선_구간생성_요청(이호선, sectionRequest);

        // when
        ExtractableResponse<Response> 지하철노선_역삭제_응답 = 지하철노선_역삭제_요청(이호선, 영등포구청역);

        // then
        삭제요청_성공(지하철노선_역삭제_응답);
    }
}
