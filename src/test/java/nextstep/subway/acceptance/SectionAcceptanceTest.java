package nextstep.subway.acceptance;

import static nextstep.subway.utils.LineTestRequests.지하철_노선_조회_응답값_반환;
import static nextstep.subway.utils.LineTestRequests.지하철_노선도_등록;
import static nextstep.subway.utils.SectionTestRequests.지하철_구간_등록;
import static nextstep.subway.utils.SectionTestRequests.지하철_구간_삭제;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록_Id_획득;
import static nextstep.subway.utils.StatusCodeAssertions.에러코드_검증;
import static nextstep.subway.utils.StatusCodeAssertions.응답코드_검증;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.utils.DBCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SectionAcceptanceTest {

    @Autowired
    private DBCleanup dbCleanup;
    private Long 첫번째역;
    private Long 두번째역;
    private Long 세번째역;
    private Long 네번째역;

    @BeforeEach
    void init() {
        dbCleanup.execute();
        첫번째역 = 지하철_역_등록_Id_획득("첫번째역");
        두번째역 = 지하철_역_등록_Id_획득("두번째역");
        세번째역 = 지하철_역_등록_Id_획득("세번째역");
        네번째역 = 지하철_역_등록_Id_획득("네번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간을 생성한다. - 새로운 역이 역 사이일 때
     * Then 생성한 노선의 하행선이 변경된다.
     */
    @DisplayName("지하철 구간을 생성한다. - 새로운 역이 역 사이일 때")
    @Test
    void createSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 세번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 첫번째역, 두번째역, 4);

        //then
        응답코드_검증(response, HttpStatus.OK);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        상행_종점역_기대값_검증(line7, 첫번째역, "첫번째역");
        하행_종점역_기대값_검증(line7, 세번째역, "세번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간을 생성한다. - 새로운 역이 상행 종점일 때에
     * Then 생성한 노선의 상행선이 변경된다.
     */
    @DisplayName("지하철 구간을 생성한다. - 새로운 역이 상행 종점일 때에")
    @Test
    void createSectionWhenNewStationIsLastUpward() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 세번째역, 첫번째역, 7);

        //then
        응답코드_검증(response, HttpStatus.OK);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        상행_종점역_기대값_검증(line7, 세번째역, "세번째역");
        하행_종점역_기대값_검증(line7, 두번째역, "두번째역");
    }


    /**
     * Given 노선을 생성한다
     * When 구간을 생성한다. - 새로운 역이 하행 종점일 때에
     * Then 생성한 노선의 하행선이 변경된다.
     */
    @DisplayName("지하철 구간을 생성한다. - 새로운 역이 하행 종점일 때에")
    @Test
    void createSectionWhenNewStationIsLastDownward() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 두번째역, 세번째역, 7);

        //then
        응답코드_검증(response, HttpStatus.OK);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        상행_종점역_기대값_검증(line7, 첫번째역, "첫번째역");
        하행_종점역_기대값_검증(line7, 세번째역, "세번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 역사이 새로운 역을 추가 할 때에 distance 가 들어갈 구간의 길이보다 크다면
     * Then 에러 상태 값을 리턴한다
     */
    @DisplayName("지하철 구간을 생성한다. 역사이 새로운 역을 추가 할 때에 distance 가 들어갈 구간의 길이보다 클 때에 에러 반환")
    @Test
    void createSectionExceptionWhenUpwardDoesNotMatchWithDownward() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 세번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 첫번째역, 두번째역, 5);

        //then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
        에러코드_검증(response, ErrorCode.INVALID_INTER_STATION_DISTANCE);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        상행_종점역_기대값_검증(line7, 첫번째역, "첫번째역");
        하행_종점역_기대값_검증(line7, 세번째역, "세번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간의 상행, 하행 역이 모두 등록이 되어있다면
     * Then 에러 상태 값을 리턴한다
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 상행, 하행 역이 모두 등록이 되어 있다면 에러 반환")
    @Test
    void createSectionExceptionWhenAlreadyDownwardStationRegistered() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 두번째역, 첫번째역, 4);

        //then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
        에러코드_검증(response, ErrorCode.ALREADY_IN_LINE);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        상행_종점역_기대값_검증(line7, 첫번째역, "첫번째역");
        하행_종점역_기대값_검증(line7, 두번째역, "두번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간의 상행역과 하행역 둘 중 하나도 포함이 안되어있다면
     * Then 에러 상태 값을 리턴한다
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 상행역과 하행역 둘 중 하나도 포함이 안되어있다면")
    @Test
    void createSectionExceptionWhenAlreadyDownwardStationRegisteredInOtherSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 세번째역, 네번째역, 4);

        //then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
        에러코드_검증(response, ErrorCode.NOT_FOUND);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        상행_종점역_기대값_검증(line7, 첫번째역, "첫번째역");
        하행_종점역_기대값_검증(line7, 두번째역, "두번째역");
    }

    /**
     * Given 노선을 생성한다, 구간을 생성한다.
     * When  마지막 역의 구간을 삭제한다.
     * Then  노선의 하행선이 기존 구간을 생성하기 전과 동일해진다.
     */
    @DisplayName("마지막 역의 구간을 삭제한다.")
    @Test
    void deleteSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);
        지하철_구간_등록(1L, 두번째역, 세번째역, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 세번째역);

        //then
        응답코드_검증(response, HttpStatus.NO_CONTENT);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행_종점역_기대값_검증(line7, 두번째역, "두번째역");
    }

    /**
     * Given 노선을 생성한다, 구간을 생성한다.
     * When  노선 하행선 마지막 구간이 아닌 중간에 있는 역을 삭제한다.
     * Then  제거 및 구간이 1개로 변경된다.
     */
    @DisplayName("노선 하행선 마지막 구간이 아닌 중간에 있는 역을 삭제한다. 제거 및 구간이 1개로 변경된다.")
    @Test
    void deleteSectionExceptionWhenNotDownLastStation() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);
        지하철_구간_등록(1L, 두번째역, 세번째역, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 두번째역);

        //then
        응답코드_검증(response, HttpStatus.NO_CONTENT);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행_종점역_기대값_검증(line7, 세번째역, "세번째역");
    }

    /**
     * Given 노선을 생성한다.
     * When  노선에서 구간이 하나일 때 역을 삭제한다.
     * Then  제거 불가능한 에러를 반환한다.
     */
    @DisplayName("노선의 구간이 하나일 때 삭제할 때 에러를 반환한다.")
    @Test
    void deleteSectionExceptionLastSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 두번째역);

        //then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);


        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행_종점역_기대값_검증(line7, 두번째역, "두번째역");
    }

    /**
     * Given 노선을 생성한다.
     * When  존재하지 않는 노선에서 역을 삭제한다.
     * Then  제거 불가능한 에러를 반환한다.
     */
    @DisplayName("존재하지 않는 노선에서 역을 삭제할 때 에러를 반환한다.")
    @Test
    void deleteSectionExceptionWhenDoesNotHaveLine() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);
        지하철_구간_등록(1L, 두번째역, 세번째역, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(2L, 세번째역);

        //then
        응답코드_검증(response, HttpStatus.NOT_FOUND);
    }


    /**
     * Given 노선을 생성한다.
     * When  노선에서 존재하지 않는 역을 삭제한다.
     * Then  제거 불가능한 에러를 반환한다.
     */
    @DisplayName("노선에서 존재하지 않는 역을 삭제할 때 에러를 반환한다.")
    @Test
    void deleteSectionExceptionWhenDoesNotHaveStation() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 첫번째역, 두번째역, 5);
        지하철_구간_등록(1L, 두번째역, 3L, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 네번째역);

        //then
        응답코드_검증(response, HttpStatus.BAD_REQUEST);
        에러코드_검증(response, ErrorCode.NOT_FOUND);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행_종점역_기대값_검증(line7, 세번째역, "세번째역");
    }

    private void 상행_종점역_기대값_검증(LineResponse response, Long stationId, String stationName) {
        StationResponse upwardLastStation = response.getStations().get(0);
        assertAll(
                () -> assertThat(upwardLastStation.getId()).isEqualTo(stationId),
                () -> assertThat(upwardLastStation.getName()).isEqualTo(stationName)
        );
    }

    private void 하행_종점역_기대값_검증(LineResponse response, Long stationId, String stationName) {
        StationResponse downwardLastStation = response.getStations().get(1);
        assertAll(
            () -> assertThat(downwardLastStation.getId()).isEqualTo(stationId),
            () -> assertThat(downwardLastStation.getName()).isEqualTo(stationName)
        );
    }
}
