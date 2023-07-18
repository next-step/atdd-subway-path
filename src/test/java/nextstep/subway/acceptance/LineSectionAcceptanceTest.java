package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.steps.LineSteps.*;
import static nextstep.subway.acceptance.steps.StationSteps.역_생성_ID_추출;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.LineFixture.*;
import static nextstep.subway.fixture.StationFixture.*;
import static nextstep.subway.utils.CustomAssertions.상태코드_확인;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    Long STATION_ID_1;
    Long STATION_ID_2;
    Long STATION_ID_3;
    Long LINE_ID_1;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        STATION_ID_1 = 역_생성_ID_추출(지하철역_생성_요청(강남역));
        STATION_ID_2 = 역_생성_ID_추출(지하철역_생성_요청(역삼역));
        STATION_ID_3 = 역_생성_ID_추출(지하철역_생성_요청(선릉역));
        LINE_ID_1 = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선, 빨간색, STATION_ID_1, STATION_ID_2, DISTANCE_10));
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(LINE_ID_1, STATION_ID_2, STATION_ID_3, DISTANCE_10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(LINE_ID_1);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선의_역이름_목록_추출(response)).containsExactly(강남역, 역삼역, 선릉역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(LINE_ID_1, STATION_ID_2, STATION_ID_3, DISTANCE_10);

        // when
        지하철_노선에_지하철_구간_제거_요청(LINE_ID_1, STATION_ID_3);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(LINE_ID_1);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(노선의_역이름_목록_추출(response)).containsExactly(강남역, 역삼역);
    }
}
