package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.config.annotations.AcceptanceTest;
import nextstep.config.fixtures.LineFixture;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nextstep.config.fixtures.StationFixture.역_10개;
import static nextstep.subway.steps.StationLineSteps.*;
import static nextstep.subway.steps.StationSteps.*;
import static nextstep.subway.utils.HttpResponseUtils.getCreatedLocationId;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationLineAcceptanceTest {

    Long 가산디지털단지역_번호;
    Long 구로디지털단지역_번호;
    Long 독산역_번호;
    Long 신도림역_번호;
    Long 홍대입구역_번호;
    Long 종각역_번호;
    Long 신림역_번호;
    Long 잠실역_번호;
    Long 교대역_번호;
    Long 서울역_번호;


    @BeforeEach
    void 초기_지하철_역_설정() {
        지하철_역_생성_요청(역_10개);

        Map<String, Long> 지하철역_맵 = convertStationResponses(지하철_역_목록_조회().jsonPath());
        가산디지털단지역_번호 = 지하철역_맵.get("가산디지털단지");
        구로디지털단지역_번호 = 지하철역_맵.get("구로디지털단지");
        독산역_번호 = 지하철역_맵.get("독산");
        신도림역_번호 = 지하철역_맵.get("신도림");
        홍대입구역_번호 = 지하철역_맵.get("홍대입구");
        종각역_번호 = 지하철역_맵.get("종각");
        신림역_번호 = 지하철역_맵.get("신림");
        잠실역_번호 = 지하철역_맵.get("잠실");
        교대역_번호 = 지하철역_맵.get("교대");
        서울역_번호 = 지하철역_맵.get("서울");
    }

    /**
     * When 지하철 노선을 생성하면
     * When  지하철 노선이 생성된다
     * Then  지하철 노선 목록 조회 시 생성된 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createStationLine() {
        // given
        LineRequest 신분당선 = LineFixture.신분당선(가산디지털단지역_번호, 구로디지털단지역_번호);

        // when
        ExtractableResponse<Response> 신분당선_노선_생성_응답 = 지하철_노선_생성_요청_검증_포함(신분당선);

        // then
        assertThat(convertLineResponses(모든_지하철_노선_조회_요청())).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(List.of(신분당선));
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When  지하철 노선 목록을 조회하면
     * Then  지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllStationLine() {
        // given
        LineRequest 신분당선 = LineFixture.신분당선(가산디지털단지역_번호, 구로디지털단지역_번호);
        LineRequest 분당선 = LineFixture.신분당선(구로디지털단지역_번호, 신도림역_번호);

        ExtractableResponse<Response> 신분당선_노선_생성_응답 = 지하철_노선_생성_요청_검증_포함(신분당선);
        ExtractableResponse<Response> 분당선_노선_생성_응답 = 지하철_노선_생성_요청_검증_포함(분당선);

        // when
        assertThat(convertLineResponses(모든_지하철_노선_조회_요청())).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(List.of(신분당선, 분당선));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When  생성한 지하철 노선을 조회하면
     * Then  생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findStationLine() {
        // given
        LineRequest 신분당선 = LineFixture.신분당선(가산디지털단지역_번호, 구로디지털단지역_번호);
        ExtractableResponse<Response> 신분당선_노선_생성_응답 = 지하철_노선_생성_요청_검증_포함(신분당선);

        // when, then
        assertThat(convertLineResponse(지하철_노선_조회_요청(getCreatedLocationId(신분당선_노선_생성_응답)))).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When  생성한 지하철 노선을 수정하면
     * Then  해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateStationLine() {
        // given
        LineRequest 신분당선 = LineFixture.신분당선(가산디지털단지역_번호, 구로디지털단지역_번호);
        LineRequest 수정된_신분당선 = LineFixture.수정된_신분당선(가산디지털단지역_번호, 구로디지털단지역_번호);

        ExtractableResponse<Response> 신분당선_노선_생성_응답 = 지하철_노선_생성_요청_검증_포함(신분당선);

        // when
        지하철_노선_수정_요청(수정된_신분당선, getCreatedLocationId(신분당선_노선_생성_응답));

        // then
        assertThat(convertLineResponse(지하철_노선_조회_요청(getCreatedLocationId(신분당선_노선_생성_응답)))).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(수정된_신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When  생성한 지하철 노선을 삭제하면
     * Then  해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteStationLine() {
        // given
        LineRequest 신분당선 = LineFixture.신분당선(가산디지털단지역_번호, 구로디지털단지역_번호);
        LineRequest 분당선 = LineFixture.분당선(구로디지털단지역_번호, 신도림역_번호);
        LineRequest 신림선 = LineFixture.신림선(종각역_번호, 서울역_번호);

        ExtractableResponse<Response> 신분당선_생성요청_응답 = 지하철_노선_생성_요청_검증_포함(신분당선);
        ExtractableResponse<Response> 분당선_생성요청_응답 = 지하철_노선_생성_요청_검증_포함(분당선);
        ExtractableResponse<Response> 신림선_생성요청_응답 = 지하철_노선_생성_요청_검증_포함(신림선);

        // when
        지하철_노선_삭제_요청(getCreatedLocationId(분당선_생성요청_응답));

        // then
        assertThat(convertLineResponses(모든_지하철_노선_조회_요청())).usingRecursiveComparison()
                .ignoringFields("id", "stations")
                .isEqualTo(List.of(신분당선, 신림선));
    }

    private List<LineResponse> convertLineResponses(JsonPath jsonPath) {
        List<Long> ids = jsonPath.getList(ID_KEY, Long.class);
        List<String> names = jsonPath.getList(NAME_KEY, String.class);
        List<String> colors = jsonPath.getList(COLOR_KEY, String.class);

        return IntStream.range(0, names.size())
                .mapToObj(i -> new LineResponse(
                        ids.get(i),
                        names.get(i),
                        colors.get(i)
                ))
                .collect(Collectors.toList());
    }

    private LineResponse convertLineResponse(JsonPath jsonPath) {
        return new LineResponse(
                jsonPath.getLong(ID_KEY),
                jsonPath.get(NAME_KEY).toString(),
                jsonPath.get(COLOR_KEY).toString()
        );
    }
}
