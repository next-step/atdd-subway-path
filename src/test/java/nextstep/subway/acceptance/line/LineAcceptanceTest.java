package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.common.SubwayUtils.responseToLocation;
import static nextstep.subway.acceptance.common.SubwayUtils.responseToName;
import static nextstep.subway.acceptance.common.SubwayUtils.responseToNames;
import static nextstep.subway.acceptance.line.LineUtils.*;
import static nextstep.subway.acceptance.station.StationUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("노선 관리 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Long 신사역_id;
    private Long 논현역_id;
    private Long 강남역_id;
    private Long 역삼역_id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        신사역_id = 지하철역_생성_후_id_추출(신사역);
        논현역_id = 지하철역_생성_후_id_추출(논현역);
        강남역_id = 지하철역_생성_후_id_추출(강남역);
        역삼역_id = 지하철역_생성_후_id_추출(역삼역);
    }

    /**
     * When 새로운 지하철 노선을 입력하고, 관리자가 노선을 생성하면 Then 해당 노선이 생성된다. Then 노선 목록에 포함된다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철노선_생성(신분당선, "bg-red-600", 신사역_id, 논현역_id,
            10L);
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //then
        String 생성된_노선명 = responseToName(신분당선_생성_응답);
        assertThat(생성된_노선명).isEqualTo(신분당선);
    }

    /**
     * Given 여러개의 지하철 노선이 등록되어 있고 When 지하철 노선 목록을 조회하면 Then 모든 지하철 노선 목록이 조회된다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        //given
        ExtractableResponse<Response> 신분당선_생성_응답 = 지하철노선_생성(신분당선, "bg-red-600", 신사역_id, 논현역_id,
            10L);
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> 이호선_생성_응답 = 지하철노선_생성(이호선, "bg-red-600", 역삼역_id, 강남역_id, 10L);
        assertThat(이호선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //when
        List<String> 지하철노선명_목록 = responseToNames(지하철노선_목록조회());

        //then
        assertThat(지하철노선명_목록).containsExactlyInAnyOrder(신분당선, 이호선);

    }

    /**
     * Given 특정 지하철 노선이 등록되어 있고 When 해당 노선을 조회하면 Then 해당 지하철 노선 목록이 조회된다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        //given
        Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

        //when
        ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_조회(신분당선_id);

        //then
        String 지하철노선_이름 = responseToName(지하철노선_조회_응답);
        assertThat(지하철노선_이름).isEqualTo(신분당선);

        List<String> 지하철노선의_역이름_목록 = responseToStationNames(지하철노선_조회_응답);
        assertThat(지하철노선의_역이름_목록).containsExactlyInAnyOrder(신사역, 논현역);
    }

    /**
     * Given 특정 지하철 노선이 등록되어 있고 When 해당 노선을 수정하면 Then 해당 노선의 정보가 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        Long 신분당선_id = 지하철노선_생성_후_ID_반환(신분당선, "bg-red-600", 신사역_id, 논현역_id, 10L);

        //when
        ExtractableResponse<Response> 지하철노선_수정_응답 = 지하철노선_수정(신분당선_id, 일호선, "bg-blue-600");

        //then
        assertThat(지하철노선_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        //then
        String 수정된노선_이름 = responseToName(지하철노선_조회(신분당선_id));
        assertThat(수정된노선_이름).isEqualTo(일호선);
    }

    /**
     * Given 특정 지하철 노선이 등록되어 있고 When 해당 노선을 삭제하면 Then 해당 노선이 삭제되고 노선 목록에서 제외된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        ExtractableResponse<Response> 지하철노선_생성_응답 = 지하철노선_생성_후_검증(신분당선, "bg-red-600", 신사역_id,
            논현역_id, 10L);

        //when
        ExtractableResponse<Response> 지하철노선_삭제_응답 = 지하철노선_삭제(responseToLocation(지하철노선_생성_응답));

        //then
        assertThat(지하철노선_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        //then
        List<String> 지하철노선_목록 = responseToNames(지하철노선_목록조회());
        assertThat(지하철노선_목록).doesNotContain(신분당선);

    }

}
