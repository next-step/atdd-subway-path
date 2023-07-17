package nextstep.subway.acceptance.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.acceptance.station.StationFixture;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.line.dto.request.SaveLineRequestDto;
import nextstep.subway.line.dto.request.SaveLineSectionRequestDto;
import nextstep.subway.line.dto.response.LineResponseDto;
import nextstep.subway.station.dto.request.SaveStationRequestDto;
import nextstep.subway.station.dto.response.StationResponseDto;
import nextstep.subway.support.AcceptanceTest;
import nextstep.subway.support.DatabaseCleanup;
import nextstep.subway.support.RestAssuredClient;
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

    private static final String LINE_BASE_URL = Endpoint.LINE_BASE_URL.getUrl();

    private static final String ERROR_MESSAGES_KEY = "errorMessages";

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

        this.신사역_아이디 = saveStation(StationFixture.신사역);
        this.강남역_아이디 = saveStation(StationFixture.강남역);
        this.판교역_아이디 = saveStation(StationFixture.판교역);
        this.광교역_아이디 = saveStation(StationFixture.광교역);

        this.신분당선 = saveLine(SaveLineRequestDto.builder()
                .name("신분당선")
                .color("#f5222d")
                .distance(15)
                .upStationId(this.신사역_아이디)
                .downStationId(this.강남역_아이디)
                .build());
    }

    /**
     * <pre>
     * When 지하철 노선의 구간을 추가하면
     * Then 지하철 노선의 하행 종점역은 추가한 구간의 하행 종점역이다.
     * </pre>
     */
    @DisplayName("지하철 노선의 구간을 추가한다.")
    @Test
    void addLineSection() {
        // when
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(신분당선)
        );
        ExtractableResponse<Response> saveLineSectionResponse = saveLineSection(광교역이_하행_종점역인_구간);

        // then
        assertAll(
                () -> assertThat(saveLineSectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> {
                    LineResponseDto 추가한_지하철_구간 = saveLineSectionResponse.as(LineResponseDto.class);

                    assertThat(지하철_노선의_하행_종점역_아이디를_찾는다(추가한_지하철_구간))
                            .isEqualTo(광교역이_하행_종점역인_구간.getDownStationId());
                }
        );

    }

    /**
     * <pre>
     * When 지하철 노선에 포함되어 있는 지하철역을 새로운 구간의 하행 종점역으로 생성하면
     * Then 구간 생성에 실패한다.
     * </pre>
     */
    @DisplayName("이미 등록되어 있는 역이 하행 종점역인 구간을 추가한다.")
    @Test
    void addAlreadyRegisteredDownStation() {
        // given
        SaveLineSectionRequestDto 판교역이_하행_종점역인_구간 = 판교역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(신분당선)
        );
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(
                판교역이_하행_종점역인_구간.getDownStationId()
        );
        List.of(판교역이_하행_종점역인_구간, 광교역이_하행_종점역인_구간).forEach(this::saveLineSection);

        // when
        SaveLineSectionRequestDto 판교역이_다시_하행_종점역인_구간 = 판교역이_하행_종점역인_구간을_생성한다(
                광교역이_하행_종점역인_구간.getDownStationId()
        );
        ExtractableResponse<Response> saveLineSectionResponse = saveLineSection(판교역이_다시_하행_종점역인_구간);

        // then
        assertAll(
                () -> assertThat(saveLineSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(
                        saveLineSectionResponse
                                .jsonPath()
                                .getList(ERROR_MESSAGES_KEY, String.class))
                        .containsAnyOf(ErrorCode.ALREADY_REGISTERED_STATION.getMessage())
        );
    }

    /**
     * <pre>
     * When 존재하지 않는 지하철역을 새로운 구간의 하행 종점역으로 추가하면
     * Then 구간 생성에 실패한다.
     * </pre>
     */
    @DisplayName("존재하지 않는 역이 하행 종점역인 구간을 추가한다.")
    @Test
    void createNotExistDownStation() {
        // when
        SaveLineSectionRequestDto 존재하지_않는_역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .downStationId(지하철_노선의_하행_종점역_아이디를_찾는다(신분당선))
                .upStationId(0L)
                .distance(1)
                .build();
        ExtractableResponse<Response> saveLineSectionResponse = saveLineSection(존재하지_않는_역이_하행_종점역인_구간);

        // then
        assertAll(
                () -> assertThat(saveLineSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(
                        saveLineSectionResponse
                                .jsonPath()
                                .getList(ERROR_MESSAGES_KEY, String.class))
                        .containsAnyOf(ErrorCode.NOT_EXIST_STATION.getMessage())
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
        saveLineSection(광교역이_하행_종점역인_구간);

        // when
        ExtractableResponse<Response> deleteLineSectionByStationIdResponse =
                deleteLineSectionByStationId(광교역이_하행_종점역인_구간.getDownStationId());

        // then
        assertAll(
                () -> assertThat(deleteLineSectionByStationIdResponse.statusCode())
                        .isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> {
                    List<Long> stationIds = RestAssuredClient.get(
                            String.format("%s/%d", LINE_BASE_URL, 신분당선.getId()))
                            .jsonPath()
                            .getList("stations", StationResponseDto.class)
                            .stream()
                            .map(StationResponseDto::getId)
                            .collect(Collectors.toList());

                    assertThat(stationIds).doesNotContain(광교역이_하행_종점역인_구간.getDownStationId());
                }
        );
    }

    /**
     * <pre>
     * Given 지하철 노선의 구간을 추가하고
     * When 중간 구간을 삭제하면
     * Then 구간 삭제에 실패한다.
     * </pre>
     */
    @DisplayName("지하철 노선의 중간 구간을 삭제한다.")
    @Test
    void deleteMiddleLineSection() {
        // given
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = 광교역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(신분당선)
        );
        saveLineSection(광교역이_하행_종점역인_구간);

        // when
        ExtractableResponse<Response> deleteLineSectionByStationIdResponse =
                deleteLineSectionByStationId(광교역이_하행_종점역인_구간.getUpStationId());

        // then
        assertAll(
                () -> assertThat(deleteLineSectionByStationIdResponse.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(
                        deleteLineSectionByStationIdResponse
                                .jsonPath()
                                .getList(ERROR_MESSAGES_KEY, String.class))
                        .containsAnyOf(ErrorCode.IS_NOT_LAST_LINE_SECTION.getMessage())
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
        saveLineSection(광교역이_하행_종점역인_구간);

        // when
        ExtractableResponse<Response> deleteLineSectionByStationIdResponse =
                deleteLineSectionByStationId(판교역_아이디);

        // then
        assertAll(
                () -> assertThat(deleteLineSectionByStationIdResponse.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(
                        deleteLineSectionByStationIdResponse
                                .jsonPath()
                                .getList(ERROR_MESSAGES_KEY, String.class))
                        .containsAnyOf(ErrorCode.UNREGISTERED_STATION.getMessage())
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
                deleteLineSectionByStationId(지하철_노선의_하행_종점역_아이디를_찾는다(신분당선));

        // then
        assertAll(
                () -> assertThat(deleteLineSectionByStationIdResponse.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(
                        deleteLineSectionByStationIdResponse
                                .jsonPath()
                                .getList(ERROR_MESSAGES_KEY, String.class))
                        .containsAnyOf(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage())
        );
    }

    /**
     * <pre>
     * 지하철역을 생성하는 API를 호출하고
     * 저장된 지하철역의 id를 반환하는 함수
     * </pre>
     *
     * @param station
     * @return saved station id
     */
    private Long saveStation(SaveStationRequestDto station) {
        return RestAssuredClient.post(Endpoint.STATION_BASE_URL.getUrl(), station)
                .jsonPath()
                .getLong("id");
    }

    /**
     * <pre>
     * 지하철 노선을 생성하는 API를 호출하고
     * 저장된 지하철 노선을 반환하는 함수
     * </pre>
     *
     * @param line
     * @return saved line id
     */
    private LineResponseDto saveLine(SaveLineRequestDto line) {
        return RestAssuredClient.post(LINE_BASE_URL, line)
                .as(LineResponseDto.class);
    }

    /**
     * <pre>
     * 지하철 노선 구간을 생성하는 API를 호출하는 함수
     * </pre>
     *
     * @param lineSection
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> saveLineSection(SaveLineSectionRequestDto lineSection) {
        return RestAssuredClient.post(
                String.format(
                        "%s/%d/sections",
                        LINE_BASE_URL,
                        this.신분당선.getId()),
                lineSection
        );
    }

    /**
     * <pre>
     * 지하철역 id로
     * 지하철 노선 구간을 삭제하는 API를 호출하는 함수
     * </pre>
     *
     * @param stationId
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> deleteLineSectionByStationId(Long stationId) {
        return RestAssuredClient.delete(String
                .format("%s/%d/sections?stationId=%d", LINE_BASE_URL, this.신분당선.getId(), stationId)
        );
    }

    private SaveLineSectionRequestDto 판교역이_하행_종점역인_구간을_생성한다(Long upStationId) {
        return SaveLineSectionRequestDto.builder()
                .upStationId(upStationId)
                .downStationId(this.판교역_아이디)
                .distance(4)
                .build();
    }

    private SaveLineSectionRequestDto 광교역이_하행_종점역인_구간을_생성한다(Long upStationId) {
        return SaveLineSectionRequestDto.builder()
                .upStationId(upStationId)
                .downStationId(this.광교역_아이디)
                .distance(8)
                .build();
    }

    private Long 지하철_노선의_하행_종점역_아이디를_찾는다(LineResponseDto lineResponseDto) {
        List<StationResponseDto> stations = lineResponseDto.getStations();
        int lastIndex = stations.size() - 1;
        return stations
                .get(lastIndex)
                .getId();
    }

}
