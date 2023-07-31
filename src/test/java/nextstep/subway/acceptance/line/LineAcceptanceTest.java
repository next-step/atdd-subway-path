package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step.LineAcceptanceStep;
import nextstep.subway.acceptance.step.StationAcceptanceStep;
import nextstep.subway.fixture.StationFixture;
import nextstep.subway.line.dto.request.SaveLineRequestDto;
import nextstep.subway.line.dto.request.UpdateLineRequestDto;
import nextstep.subway.station.dto.response.StationResponseDto;
import nextstep.subway.support.AcceptanceTest;
import nextstep.subway.support.AssertUtils;
import nextstep.subway.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.UNDEFINED_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@AcceptanceTest
public class LineAcceptanceTest {

    @LocalServerPort
    private int port;

    private static final String LINE_ID_KEY = "id";

    private static final String LINE_NAME_KEY = "name";

    private Long 신사역_아이디;

    private Long 광교역_아이디;

    private Long 청량리역_아이디;

    private Long 춘천역_아이디;

    @Autowired
    private DatabaseCleanup databaseCleanUp;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanUp.execute();

        this.신사역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.신사역)
                .as(StationResponseDto.class)
                .getId();
        this.광교역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.광교역)
                .as(StationResponseDto.class)
                .getId();
        this.청량리역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.청량리역)
                .as(StationResponseDto.class)
                .getId();
        this.춘천역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.춘천역)
                .as(StationResponseDto.class)
                .getId();
    }

    /**
     * <pre>
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * </pre>
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        SaveLineRequestDto 신분당선 = 신분당선을_생성한다(신사역_아이디, 광교역_아이디);
        LineAcceptanceStep.지하철_노선_생성을_요청한다(신분당선);

        // then
        List<String> 지하철_노선_이름_목록 = LineAcceptanceStep.지하철_노선_목록_조회를_요청한다()
                .jsonPath()
                .getList(LINE_NAME_KEY, String.class);

        assertAll(
                () -> assertThat(지하철_노선_이름_목록.size()).isEqualTo(1),
                () -> assertThat(지하철_노선_이름_목록).containsAnyOf(신분당선.getName())
        );
    }

    /**
     * <pre>
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     * </pre>
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void readLines() {
        // given
        SaveLineRequestDto 신분당선 = 신분당선을_생성한다(신사역_아이디, 광교역_아이디);
        SaveLineRequestDto 경춘선 = 경춘선을_생성한다(청량리역_아이디, 춘천역_아이디);
        Stream.of(신분당선, 경춘선).forEach(LineAcceptanceStep::지하철_노선_생성을_요청한다);

        // when
        List<String> 지하철_노선_이름_목록 = LineAcceptanceStep.지하철_노선_목록_조회를_요청한다()
                .jsonPath()
                .getList(LINE_NAME_KEY, String.class);

        // then
        assertThat(지하철_노선_이름_목록).containsOnly(신분당선.getName(), 경춘선.getName());
    }

    /**
     * <pre>
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * </pre>
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void readLine() {
        // given
        SaveLineRequestDto 경춘선 = 경춘선을_생성한다(청량리역_아이디, 춘천역_아이디);
        Long 저장된_노선_아이디 = LineAcceptanceStep.지하철_노선_생성을_요청한다(경춘선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        // when
        String 조회한_노선의_이름 = LineAcceptanceStep.지하철_노선_상세_조회를_요청한다(저장된_노선_아이디)
                .jsonPath()
                .getString(LINE_NAME_KEY);

        // then
        assertThat(조회한_노선의_이름).isEqualTo(경춘선.getName());
    }

    /**
     * <pre>
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * </pre>
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        SaveLineRequestDto 신분당선 = 신분당선을_생성한다(신사역_아이디, 광교역_아이디);
        Long 저장된_노선의_아이디 = LineAcceptanceStep.지하철_노선_생성을_요청한다(신분당선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        // when
        ExtractableResponse<Response> 지하철_노선_수정_응답 =
                LineAcceptanceStep.지하철_노선_수정을_요청한다(수정한_신분당선, 저장된_노선의_아이디);

        // then
        String 수정된_노선의_이름 = LineAcceptanceStep.지하철_노선_상세_조회를_요청한다(저장된_노선의_아이디)
                .jsonPath()
                .getString(LINE_NAME_KEY);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철_노선_수정_응답, HttpStatus.OK),
                () -> assertThat(수정된_노선의_이름).isEqualTo(수정한_신분당선.getName())
        );
    }

    /**
     * <pre>
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * </pre>
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        SaveLineRequestDto 경춘선 = 경춘선을_생성한다(청량리역_아이디, 춘천역_아이디);
        Long 저장된_노선의_아이디 = LineAcceptanceStep.지하철_노선_생성을_요청한다(경춘선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        // when
        ExtractableResponse<Response> 지하철_노선_삭제_응답 =
                LineAcceptanceStep.지하철_노선_삭제를_요청한다(저장된_노선의_아이디);

        // then
        List<String> 지하철_노선_이름_목록 = LineAcceptanceStep.지하철_노선_목록_조회를_요청한다()
                .jsonPath()
                .getList(LINE_NAME_KEY, String.class);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철_노선_삭제_응답, HttpStatus.NO_CONTENT),
                () -> assertThat(지하철_노선_이름_목록).doesNotContain(경춘선.getName())
        );
    }

    private SaveLineRequestDto 신분당선을_생성한다(Long upStationId, Long downStationId) {
        return SaveLineRequestDto.builder()
                .name("신분당선")
                .color("#f5222d")
                .distance(15)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build();
    }

    private SaveLineRequestDto 경춘선을_생성한다(Long upStationId, Long downStationId) {
        return SaveLineRequestDto.builder()
                .name("경춘선")
                .color("#13c2c2")
                .distance(25)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build();
    }

    private final UpdateLineRequestDto 수정한_신분당선 = UpdateLineRequestDto.builder()
                    .name("수정한 신분당선")
                    .color("#cf1322")
                    .build();

}
