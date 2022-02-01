package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.commons.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineTestRequest;
import nextstep.subway.line.dto.SectionTestRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.commons.AssertionsUtils.요청_실패;
import static nextstep.subway.line.acceptance.LineUtils.지하철노선_단건조회_요청;
import static nextstep.subway.line.acceptance.LineUtils.지하철노선_생성_요청;
import static nextstep.subway.line.acceptance.SectionUtils.*;
import static nextstep.subway.station.acceptance.StationUtils.getStationId;
import static nextstep.subway.station.acceptance.StationUtils.지하철역_생성요청;

@DisplayName("지하철 노선의 구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    long 이호선;
    long 이호선_상행종점역;
    long 이호선_하행종점역;
    int 이호선_구간길이 = 7;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        LineTestRequest request = LineTestRequest.builder()
                 .lineName("2호선")
                 .lineColor("bg-green")
                 .upStationName("신도림역")
                 .downStationName("영등포구청역")
                 .distance(이호선_구간길이)
                 .build();

        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        이호선 = response.as(LineResponse.class).getId();
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
        long 구간_하행역 = getStationId(지하철역_생성요청("당산역"));

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(이호선_하행종점역)
                .downStationId(구간_하행역)
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(request);
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
        long 구간_상행역 = getStationId(지하철역_생성요청("대림역"));

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(구간_상행역)
                .downStationId(이호선_상행종점역)
                .distance(7)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(request);
        ExtractableResponse<Response> 지하철노선_단건조회_응답 = 지하철노선_단건조회_요청(이호선);

        // then
        지하철노선_구간생성_요청_성공(지하철노선_구간생성_응답, 지하철노선_단건조회_응답);
        지하철노선_상행종점역_검증(구간_상행역, 지하철노선_단건조회_응답);
    }

    /**
     * Given 2호선 생성을 요청하고
     * When 새로운 구간 생성을 요청하면
     * Then 새로운 구간 생성요청이 성공한다.
     */
    @Test
    void 구간_추가() {
        // given
        long 문래역 = getStationId(지하철역_생성요청("문래역"));

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(이호선_상행종점역)
                .downStationId(문래역)
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(request);
        ExtractableResponse<Response> 지하철노선_단건조회_응답 = 지하철노선_단건조회_요청(이호선);

        // then
        지하철노선_구간생성_요청_성공(지하철노선_구간생성_응답, 지하철노선_단건조회_응답);
    }

    /**
     * Given 2호선 생성을 요청하고
     * Given 새로운 구간의 상행역과 하행역 모두 2호선에 이미 존재하고
     * When 새로운 구간 생성을 요청하면
     * Then 새로운 구간 생성요청이 실패한다.
     */
    @Test
    void 새로운구간_상행역과_하행역이_모두_노선에_존재한다(){
        // given
        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(이호선_상행종점역)
                .downStationId(이호선_하행종점역)
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(request);

        // then
        요청_실패(지하철노선_구간생성_응답);
    }

    /**
     * Given 2호선 생성을 요청하고
     * Given 새로운 구간의 상행역과 하행역 모두 2호선에 이미 존재하고
     * When 새로운 구간 생성을 요청하면
     * Then 새로운 구간 생성요청이 실패한다.
     */
    @Test
    void 새로운구간_상행역과_하행역이_모두_노선에_존재하지_않는다(){
        // given
        long 당산역 = getStationId(지하철역_생성요청("당산역"));
        long 합정역 = getStationId(지하철역_생성요청("합정역"));

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(당산역)
                .downStationId(합정역)
                .distance(3)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(request);

        // then
        요청_실패(지하철노선_구간생성_응답);
    }

    /**
     * Given 2호선 생성을 요청하고
     * Given 새로운 구간의 길이가 2호선 구간의 길이와 같고
     * When 새로운 구간 생성을 요청하면
     * Then 새로운 구간 생성요청이 실패한다.
     */
    @Test
    void 구간의_길이가_같으면_구간생성_실패한다() {
        // given
        long 문래역 = getStationId(지하철역_생성요청("문래역"));

        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(이호선_상행종점역)
                .downStationId(문래역)
                .distance(이호선_구간길이)
                .build();

        // when
        ExtractableResponse<Response> 지하철노선_구간생성_응답 = 지하철노선_구간생성_요청(request);

        // then
        요청_실패(지하철노선_구간생성_응답);
    }

    /**
     * Given 2호선 생성을 요청하고
     * Given 새로운 구간 생성을 요청하고
     * When 역 삭제 요청을 하면
     * Then 역 삭제 요청이 성공한다.
     */
    @Test
    void 역_삭제() {
        // given
        long 문래역 = getStationId(지하철역_생성요청("문래역"));
        SectionTestRequest request = SectionTestRequest.builder()
                .lineId(이호선)
                .upStationId(이호선_상행종점역)
                .downStationId(문래역)
                .distance(3)
                .build();

        지하철노선_구간생성_요청(request);

        // when
        ExtractableResponse<Response> 역_삭제_응답 = 지하철역_삭제_요청(이호선, 문래역);
        ExtractableResponse<Response> 지하철노선_단건조회_응답 = 지하철노선_단건조회_요청(이호선);

        // then
        지하철역_삭제_요청_성공(역_삭제_응답, 지하철노선_단건조회_응답);
    }
}
