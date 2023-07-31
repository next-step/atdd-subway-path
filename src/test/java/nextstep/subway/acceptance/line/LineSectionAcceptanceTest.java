package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.acceptance.step.LineAcceptanceStep;
import nextstep.subway.acceptance.step.LineSectionAcceptanceStep;
import nextstep.subway.acceptance.step.StationAcceptanceStep;
import nextstep.subway.fixture.StationFixture;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.line.dto.request.SaveLineRequestDto;
import nextstep.subway.line.dto.request.SaveLineSectionRequestDto;
import nextstep.subway.line.dto.response.LineResponseDto;
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
import java.util.stream.Collectors;

import static io.restassured.RestAssured.UNDEFINED_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선의 구간 관련 기능")
@AcceptanceTest
public class LineSectionAcceptanceTest {

    @LocalServerPort
    private int port;

    private Long 신사역_아이디;

    private Long 강남역_아이디;

    private Long 판교역_아이디;

    private Long 광교역_아이디;

    private LineResponseDto 신분당선;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        this.신사역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.신사역)
                .as(StationResponseDto.class)
                .getId();
        this.강남역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.강남역)
                .as(StationResponseDto.class)
                .getId();
        this.판교역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.판교역)
                .as(StationResponseDto.class)
                .getId();
        this.광교역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.광교역)
                .as(StationResponseDto.class)
                .getId();

        SaveLineRequestDto 저장할_신분당선 = SaveLineRequestDto.builder()
                .name("신분당선")
                .color("#f5222d")
                .distance(7)
                .upStationId(this.신사역_아이디)
                .downStationId(this.판교역_아이디)
                .build();
        this.신분당선 = LineAcceptanceStep.지하철_노선_생성을_요청한다(저장할_신분당선).as(LineResponseDto.class);
    }

    /**
     * <pre>
     * Given 신사역 - 판교역 구간을 가지고 있는 신분당선에
     * When 강남역 - 신사역 구간을 추가하면
     * Then 지하철 노선은 강남역 - 신사역 - 판교역 노선을 가진다.
     * </pre>
     */
    @DisplayName("지하철 노선의 상행 종점역이 하행역인 구간을 추가한다.")
    @Test
    void addFirstLineSection() {
        // when
        SaveLineSectionRequestDto 강남역_신사역_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(강남역_아이디)
                .downStationId(신사역_아이디)
                .distance(3)
                .build();
        ExtractableResponse<Response> 지하쳘_구간_생성_응답 =
                LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(강남역_신사역_구간, 신분당선.getId());

        // then
        LineResponseDto 지하철_노선 = 지하쳘_구간_생성_응답.as(LineResponseDto.class);
        List<Long> 등록된_지하철역_아이디_목록 = 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(지하철_노선);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하쳘_구간_생성_응답, HttpStatus.CREATED),
                () -> assertThat(등록된_지하철역_아이디_목록).containsExactly(강남역_아이디, 신사역_아이디, 판교역_아이디)
        );
    }

    /**
     * <pre>
     * Given 신사역 - 판교역 구간을 가지고 있는 신분당선에
     * When 신사역 - 강남역 구간을 추가하면
     * Then 지하철 노선은 신사역 - 강남역 - 판교역 노선을 가진다.
     * </pre>
     */
    @DisplayName("지하철 노선의 상행 종점역과 하행 종점역 사이에 구간을 추가한다.")
    @Test
    void addMiddleLineSection() {
        // when
        SaveLineSectionRequestDto 신사역_강남역_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(신사역_아이디)
                .downStationId(강남역_아이디)
                .distance(3)
                .build();
        ExtractableResponse<Response> 지하쳘_노선_생성_응답 =
                LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(신사역_강남역_구간, 신분당선.getId());

        // then
        LineResponseDto 지하철_노선 = 지하쳘_노선_생성_응답.as(LineResponseDto.class);
        List<Long> 등록된_지하철역_아이디_목록 = 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(지하철_노선);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하쳘_노선_생성_응답, HttpStatus.CREATED),
                () -> assertThat(등록된_지하철역_아이디_목록).containsExactly(신사역_아이디, 강남역_아이디, 판교역_아이디)
        );
    }

    /**
     * <pre>
     * Given 신사역 - 판교역 구간을 가지고 있는 신분당선에
     * When 판교역 - 광교역 구간을 추가하면
     * Then 신분당선은 신사역 - 판교역 - 광교역 노선을 가진다.
     * </pre>
     */
    @DisplayName("지하철 노선의 하행 종점역에 구간을 추가한다.")
    @Test
    void addLastLineSection() {
        // when
        SaveLineSectionRequestDto 판교역_광교역_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(판교역_아이디)
                .downStationId(광교역_아이디)
                .distance(8)
                .build();
        ExtractableResponse<Response> 지하쳘_노선_생성_응답 =
                LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(판교역_광교역_구간, 신분당선.getId());

        // then
        LineResponseDto 지하철_노선 = 지하쳘_노선_생성_응답.as(LineResponseDto.class);
        List<Long> 등록된_지하철역_아이디_목록 = 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(지하철_노선);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하쳘_노선_생성_응답, HttpStatus.CREATED),
                () -> assertThat(등록된_지하철역_아이디_목록).containsExactly(신사역_아이디, 판교역_아이디, 광교역_아이디)
        );

    }

    /**
     * <pre>
     * Given 지하철 노선의 구간을 추가하고
     * When 첫번째 구간을 삭제하면
     * Then 지하철 노선 상세 조회 시 첫번째 구간이 존재하지 않는다.
     * </pre>
     */
    @DisplayName("지하철 노선의 첫번째 구간을 삭제한다.")
    @Test
    void deleteFirstLineSection() {
        // given
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(신분당선)
        );
        LineResponseDto 광교역이_하행_종점역으로_추가된_신분당선 =
                LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(광교역이_하행_종점역인_구간, 신분당선.getId())
                .as(LineResponseDto.class);

        // when
        Long 신분당선의_상행_종점역_아이디 = 지하철_노선의_상행_종점역_아이디를_찾는다(광교역이_하행_종점역으로_추가된_신분당선);
        ExtractableResponse<Response> 지하철_구간_삭제_응답 =
                LineSectionAcceptanceStep.지하철_구간_삭제을_요청한다(신분당선.getId(), 신분당선의_상행_종점역_아이디);

        // then
        ExtractableResponse<Response> 삭제_후_신분당선 = LineAcceptanceStep.지하철_노선_상세_조회를_요청한다(신분당선.getId());
        List<Long> 지하철_노선에_등록된_역_아이디_목록 = 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(삭제_후_신분당선);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철_구간_삭제_응답, HttpStatus.NO_CONTENT),
                () -> assertThat(지하철_노선에_등록된_역_아이디_목록).doesNotContain(신분당선의_상행_종점역_아이디)
        );
    }

    /**
     * <pre>
     * Given 지하철 노선의 구간을 추가하고
     * When 중간 구간을 삭제하면
     * Then 지하철 노선 상세 조회 시 중간 구간이 존재하지 않는다.
     * </pre>
     */
    @DisplayName("지하철 노선의 중간 구간을 삭제한다.")
    @Test
    void deleteMiddleLineSection() {
        // given
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(판교역_아이디);
        LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(광교역이_하행_종점역인_구간, 신분당선.getId());

        // when
        ExtractableResponse<Response> 지하철_구간_삭제_응답 =
                LineSectionAcceptanceStep.지하철_구간_삭제을_요청한다(신분당선.getId(), 판교역_아이디);

        // then
        ExtractableResponse<Response> 삭제_후_신분당선 = LineAcceptanceStep.지하철_노선_상세_조회를_요청한다(신분당선.getId());
        List<Long> 지하철_노선에_등록된_역_아이디_목록 = 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(삭제_후_신분당선);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철_구간_삭제_응답, HttpStatus.NO_CONTENT),
                () -> assertThat(지하철_노선에_등록된_역_아이디_목록).doesNotContain(판교역_아이디)
        );
    }

    /**
     * <pre>
     * Given 지하철 노선의 구간을 추가하고
     * When 마지막 구간을 삭제하면
     * Then 지하철 노선 상세 조회 시 마지막 구간이 존재하지 않는다.
     * </pre>
     */
    @DisplayName("지하철 노선의 마지막 구간을 삭제한다.")
    @Test
    void deleteLastLineSection() {
        // given
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(신분당선)
        );
        LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(광교역이_하행_종점역인_구간, 신분당선.getId());

        // when
        ExtractableResponse<Response> 지하철_구간_삭제_응답 =
                LineSectionAcceptanceStep.지하철_구간_삭제을_요청한다(신분당선.getId(), 광교역이_하행_종점역인_구간.getDownStationId());

        // then
        ExtractableResponse<Response> 삭제_후_신분당선 = LineAcceptanceStep.지하철_노선_상세_조회를_요청한다(신분당선.getId());
        List<Long> 지하철_노선에_등록된_역_아이디_목록 = 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(삭제_후_신분당선);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철_구간_삭제_응답, HttpStatus.NO_CONTENT),
                () -> assertThat(지하철_노선에_등록된_역_아이디_목록).doesNotContain(광교역이_하행_종점역인_구간.getDownStationId())
        );
    }

    /**
     * <pre>
     * Given 지하철 노선의 구간을 추가하고
     * When 등록되지 않은 구간을 삭제하면
     * Then 구간 삭제에 실패한다.
     * </pre>
     */
    @DisplayName("등록되어 있지 않는 구간을 삭제한다.")
    @Test
    void deleteNotExistLineSection() {
        // given
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(신분당선)
        );
        LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(광교역이_하행_종점역인_구간, 신분당선.getId());

        // when
        ExtractableResponse<Response> 지하철_구간_삭제_응답 =
                LineSectionAcceptanceStep.지하철_구간_삭제을_요청한다(신분당선.getId(), 강남역_아이디);

        // then
        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철_구간_삭제_응답, HttpStatus.BAD_REQUEST),
                () -> AssertUtils.assertThatErrorMessage(지하철_구간_삭제_응답, ErrorCode.UNREGISTERED_STATION)
        );
    }

    /**
     * <pre>
     * When 구간을 추가하지 않고 삭제하면
     * Then 구간 삭제에 실패한다.
     * </pre>
     */
    @DisplayName("구간이 1개인 노선의 구간을 삭제한다.")
    @Test
    void deleteStandAloneLineSection() {
        // when
        ExtractableResponse<Response> deleteLineSectionByStationIdResponse =
                LineSectionAcceptanceStep.지하철_구간_삭제을_요청한다(신분당선.getId(), 지하철_노선의_하행_종점역_아이디를_찾는다(신분당선));

        // then
        assertAll(
                () -> AssertUtils.assertThatStatusCode(deleteLineSectionByStationIdResponse, HttpStatus.BAD_REQUEST),
                () -> AssertUtils.assertThatErrorMessage(deleteLineSectionByStationIdResponse, ErrorCode.STAND_ALONE_LINE_SECTION)
        );
    }

    private List<Long> 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(LineResponseDto lineResponseDto) {
        return lineResponseDto.getStations()
                .stream()
                .map(StationResponseDto::getId)
                .collect(Collectors.toList());
    }

    private List<Long> 지하철노선에_등록된_지하철역_아이디_목록을_가져온다(ExtractableResponse<Response> response) {
        return response
                .jsonPath()
                .getList("stations", StationResponseDto.class)
                .stream()
                .map(StationResponseDto::getId)
                .collect(Collectors.toList());
    }

    private SaveLineSectionRequestDto 광교역이_하행_종점역인_구간을_생성한다(Long upStationId) {
        return SaveLineSectionRequestDto.builder()
                .upStationId(upStationId)
                .downStationId(this.광교역_아이디)
                .distance(8)
                .build();
    }

    private Long 지하철_노선의_상행_종점역_아이디를_찾는다(LineResponseDto lineResponseDto) {
        List<StationResponseDto> stations = lineResponseDto.getStations();
        return stations
                .get(0)
                .getId();
    }

    private Long 지하철_노선의_하행_종점역_아이디를_찾는다(LineResponseDto lineResponseDto) {
        List<StationResponseDto> stations = lineResponseDto.getStations();
        int lastIndex = stations.size() - 1;
        return stations
                .get(lastIndex)
                .getId();
    }

}
