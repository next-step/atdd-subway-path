package nextstep.subway.line;

import com.jayway.jsonpath.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

import static nextstep.subway.line.LineTestUtils.*;
import static nextstep.subway.station.StationTestUtils.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    String 강남역_URL;
    String 판교역_URL;
    String 삼성역_URL;
    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        강남역_URL = 지하철역_생성(강남역_정보);
        판교역_URL = 지하철역_생성(판교역_정보);
        삼성역_URL = 지하철역_생성(삼성역_정보);
    }

    @DisplayName("지하철 노선을 생성한다")
    @Test
    void createLine() {
        // when
        지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 판교역_URL);

        // then
        ExtractableResponse<Response> 노선_목록_조회_결과 = 지하철_노선_목록_조회();

        Map<String, String> 강남역_저장_정보 = 역_저장_정보(강남역_정보, 지하철_아이디_획득(강남역_URL));
        Map<String, String> 판교역_저장_정보 = 역_저장_정보(판교역_정보, 지하철_아이디_획득(판교역_URL));

        모든_노선_정보가_반환된다(노선_목록_조회_결과, 신분당선_생성_요청, 강남역_저장_정보, 판교역_저장_정보);
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void getLines() {
        // given
        지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 판교역_URL);
        지하철_노선_생성(이호선_생성_요청, 강남역_URL, 삼성역_URL);

        // when
        ExtractableResponse<Response> 노선_목록_조회_결과 = 지하철_노선_목록_조회();

        // then
        Map<String, String> 강남역_저장_정보 = 역_저장_정보(강남역_정보, 지하철_아이디_획득(강남역_URL));
        Map<String, String> 판교역_저장_정보 = 역_저장_정보(판교역_정보, 지하철_아이디_획득(판교역_URL));
        Map<String, String> 삼성역_저장_정보 = 역_저장_정보(삼성역_정보, 지하철_아이디_획득(삼성역_URL));

        모든_노선_정보가_반환된다(노선_목록_조회_결과, 신분당선_생성_요청, 강남역_저장_정보, 판교역_저장_정보);
        모든_노선_정보가_반환된다(노선_목록_조회_결과, 이호선_생성_요청, 강남역_저장_정보, 삼성역_저장_정보);
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // Given
        String lineUrl = 지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 판교역_URL);

        // when
        ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회(lineUrl);

        // then
        노선_정보가_반환된다(노선_조회_결과, 신분당선_생성_요청);
    }

    @DisplayName("지하철 노선을 수정한다")
    @Test
    void changeLine() {
        // given
        Map<String, String> 노선_수정_요청_정보 = Map.of(
                "name", "다른분당선",
                "color", "bg-red-700"
        );
        String lineUrl = 지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 판교역_URL);

        // when
        지하철_노선_수정_요청(lineUrl, 노선_수정_요청_정보);

        // then
        노선_조회시_수정된_정보가_반환된다(lineUrl, 노선_수정_요청_정보);
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        String lineUrl = 지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 판교역_URL);

        // when
        지하철_노선_삭제(lineUrl);

        // then
        지하철_노선을_조회할수_없다();
    }

    private static void 모든_노선_정보가_반환된다(ExtractableResponse<Response> 노선_목록_조회_결과, Map<String, String> 노선_생성_요청_정보,
                                        Map<String, String>... 역_저장_정보들) {
        assertThat(노선_목록_조회_결과.jsonPath().getList("id")).isNotEmpty();
        assertThat(노선_목록_조회_결과.jsonPath().getList("name", String.class)).contains(노선_생성_요청_정보.get("name"));
        assertThat(노선_목록_조회_결과.jsonPath().getList("color", String.class)).contains(노선_생성_요청_정보.get("color"));

        List<Object> ids = JsonPath.parse(노선_목록_조회_결과.body().asString()).read("$.[*].stations[*].id");
        List<Object> names = JsonPath.parse(노선_목록_조회_결과.body().asString()).read("$.[*].stations[*].name");
        assertThat(ids).containsAll(
                Arrays.stream(역_저장_정보들)
                        .map(m -> m.get("id"))
                        .map(Integer::valueOf)
                        .collect(Collectors.toList())
        );
        assertThat(names).containsAll(
                Arrays.stream(역_저장_정보들)
                        .map(m -> m.get("name"))
                        .collect(Collectors.toList())
        );
    }

    private void 노선_정보가_반환된다(ExtractableResponse<Response> 노선_조회_결과, Map<String, String> 노선_생성_요청_정보) {
        assertThat(노선_조회_결과.jsonPath().getString("id")).isNotEmpty();
        assertThat(노선_조회_결과.jsonPath().getString("name")).isEqualTo(노선_생성_요청_정보.get("name"));
        assertThat(노선_조회_결과.jsonPath().getString("color")).isEqualTo(노선_생성_요청_정보.get("color"));
        assertThat(노선_조회_결과.jsonPath().getList("stations", StationResponse.class))
                .hasSize(2)
                .extracting("id").containsExactlyInAnyOrder(
                        Long.parseLong(노선_생성_요청_정보.get("upStationId")),
                        Long.parseLong(노선_생성_요청_정보.get("downStationId")));
    }


    private void 노선_조회시_수정된_정보가_반환된다(String lineUrl, Map<String, String> 노선_수정_요청_정보) {
        ExtractableResponse<Response> 노선_조회_결과 = 지하철_노선_조회(lineUrl);

        assertThat(노선_수정_요청_정보.get("name")).isEqualTo(노선_조회_결과.jsonPath().getString("name"));
        assertThat(노선_수정_요청_정보.get("color")).isEqualTo(노선_조회_결과.jsonPath().getString("color"));
    }

    private void 지하철_노선을_조회할수_없다() {
        지하철_노선_목록_조회().jsonPath().getList("id").isEmpty();
    }
}
