package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step.StationAcceptanceStep;
import nextstep.subway.fixture.StationFixture;
import nextstep.subway.station.dto.request.SaveStationRequestDto;
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

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
public class StationAcceptanceTest {

    @LocalServerPort
    private int port;


    private static final String STATION_ID_KEY = "id";

    private static final String STATION_NAME_KEY = "name";

    @Autowired private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        SaveStationRequestDto 강남역 = StationFixture.강남역;
        StationAcceptanceStep.지하철역_생성을_요청한다(강남역);

        // then
        List<String> 지하철역_이름_목록 = StationAcceptanceStep.지하철역_목록_조회를_요청한다()
                .jsonPath()
                .getList(STATION_NAME_KEY, String.class);

        assertAll(
                () -> assertThat(지하철역_이름_목록.size()).isEqualTo(1),
                () -> assertThat(지하철역_이름_목록).containsAnyOf(강남역.getName())
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void readStations() {
        //given
        SaveStationRequestDto 강남역 = StationFixture.강남역;
        SaveStationRequestDto 광교역 = StationFixture.광교역;

        Stream.of(강남역, 광교역).forEach(StationAcceptanceStep::지하철역_생성을_요청한다);

        // when
        List<String> 지하철역_이름_목록 = StationAcceptanceStep.지하철역_목록_조회를_요청한다()
                .jsonPath()
                .getList(STATION_NAME_KEY, String.class);

        // then
        assertThat(지하철역_이름_목록).containsOnly(강남역.getName(), 광교역.getName());
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        SaveStationRequestDto 강남역 = StationFixture.강남역;
        Long 저장된_지하철역_아이디 = StationAcceptanceStep.지하철역_생성을_요청한다(강남역)
                .jsonPath()
                .getLong(STATION_ID_KEY);

        // when
        ExtractableResponse<Response> 지하철역_삭제_응답 = StationAcceptanceStep
                .지하철역_삭제을_요청한다(저장된_지하철역_아이디);

        // then
        List<String> 지하철역_이름_목록 = StationAcceptanceStep.지하철역_목록_조회를_요청한다()
                .jsonPath()
                .getList(STATION_NAME_KEY, String.class);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(지하철역_삭제_응답, HttpStatus.NO_CONTENT),
                () -> assertThat(지하철역_이름_목록).doesNotContain(강남역.getName())
        );
    }

}