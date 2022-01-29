package nextstep.subway.line.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.commons.AcceptanceTest;
import nextstep.subway.commons.DatabaseCleanup;
import nextstep.subway.line.dto.LineTestRequest;
import nextstep.subway.line.dto.SectionTestRequest;
import nextstep.subway.line.utils.SectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nextstep.subway.commons.AssertionsUtils.삭제요청_성공;
import static nextstep.subway.commons.AssertionsUtils.요청_실패;
import static nextstep.subway.line.utils.LineUtils.지하철노선_단건조회_요청;
import static nextstep.subway.line.utils.LineUtils.지하철노선_생성_요청;
import static nextstep.subway.line.utils.SectionUtils.*;
import static nextstep.subway.station.acceptance.StationUtils.지하철역_생성요청;

@DisplayName("지하철 노선의 구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    long 이호선;
    long 이호선_상행종점역;
    long 이호선_하행종점역;

    @BeforeEach
    @Override
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();

        LineTestRequest request = LineTestRequest.builder()
                 .lineName("2호선")
                 .lineColor("bg-green")
                 .upStationName("신도림역")
                 .downStationName("영등포구청역")
                 .distance(7)
                 .build();

        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        이호선 = response.jsonPath().getLong("id");
        List<Integer> 이호선_역목록 = response.jsonPath().getList("stations.id");

        이호선_상행종점역 =  Long.valueOf(이호선_역목록.get(0));
        이호선_하행종점역 = Long.valueOf((이호선_역목록.get(1)));
    }

    /**
     * Given 2호선 생성을 요청하고
     * Given 새로운 구간의 상행역이 2호선의 하행종점이고
     * When 새로운 구간 생성을 요청하면
     * Then 2호선 하행종점은 새로운 구간의 하행역이 된다.
     */
    @Test
    void 새로운구간_하행역이_노선의_하행종점역이_된다() {
        // given
        long 구간_하행역 = 지하철역_생성요청("당산역").jsonPath().getLong("id");

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(이호선_하행종점역)
                .downStationId(구간_하행역)
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(이호선, request);
        ExtractableResponse<Response> 지하철노선_단건조회_응답 = 지하철노선_단건조회_요청(이호선);

        // then
        지하철노선_구간생성_요청_성공(지하철노선_구간생성_응답, 지하철노선_단건조회_응답);
        지하철노선_하행종점역_검증(구간_하행역, 지하철노선_단건조회_응답);
    }

    /**
     * Given 2호선 생성을 요청하고
     * Given 새로운 구간의 하행역이 2호선의 상행종점이고
     * When 새로운 구간 생성을 요청하면
     * Then 2호선 상행종점은 새로운 구간의 상행역이 된다.
     */
    @Test
    void 새로운구간_상행역이_노선의_상행종점역이_된다() {
        // given
        long 구간_상행역 = 지하철역_생성요청("대림역").jsonPath().getLong("id");

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(구간_상행역)
                .downStationId(이호선_상행종점역)
                .distance(7)
                .build();

        // when
        지하철노선_구간생성_요청(이호선, request);
        ExtractableResponse<Response> 지하철노선_단건조회_응답 = 지하철노선_단건조회_요청(이호선);

        // then
        지하철노선_상행종점역_검증(구간_상행역, 지하철노선_단건조회_응답);
    }

}
