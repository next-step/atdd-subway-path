package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static nextstep.subway.utils.LineTestRequests.지하철_노선_조회_응답값_반환;
import static nextstep.subway.utils.LineTestRequests.지하철_노선도_등록;
import static nextstep.subway.utils.SectionTestRequests.지하철_구간_등록;
import static nextstep.subway.utils.SectionTestRequests.지하철_구간_삭제;
import static nextstep.subway.utils.StationTestRequests.지하철_역_등록;
import static nextstep.subway.utils.StatusCodeAssertions.에러코드_검증;
import static nextstep.subway.utils.StatusCodeAssertions.응답코드_검증;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import nextstep.subway.common.exception.ErrorCode;
import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.station.controller.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SectionAcceptanceTest {

    @BeforeEach
    void initStations() {
        지하철_역_등록("첫번째역");
        지하철_역_등록("두번째역");
        지하철_역_등록("세번째역");
        지하철_역_등록("네번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간을 생성한다
     * Then 생성한 노선의 하행선이 변경된다.
     */
    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 2L, 3L, 7);

        //then
        응답코드_검증(response, HttpStatus.OK);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 3L, "세번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간의 상행이 노선의 하행이 아닐 때에
     * Then 에러 상태 값을 리턴한다
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 상행이 노선의 하행이 아닐 때에 에러 반환")
    @Test
    void createSectionExceptionWhenUpwardDoesNotMatchWithDownward() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 4L, 3L, 7);

        //then
        응답코드_검증(response, HttpStatus.OK);
        에러코드_검증(response, ErrorCode.ONLY_DOWNWARD_CAN_BE_ADDED_TO_LINE);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 2L, "두번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간의 하행이 이미 노선에 등록되어 있다면
     * Then 에러 상태 값을 리턴한다
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 하행이 이미 노선에 등록되어 있다면 에러 반환")
    @Test
    void createSectionExceptionWhenAlreadyDownwardStationRegistered() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 2L, 1L, 7);

        //then
        응답코드_검증(response, HttpStatus.OK);
        에러코드_검증(response, ErrorCode.ALREADY_IN_LINE);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 2L, "두번째역");
    }

    /**
     * Given 노선을 생성한다
     * When 구간의 하행이 이미 노선에 등록되어 있다면, 다른 구간 내에 등록 되어있을 때
     * Then 에러 상태 값을 리턴한다
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 하행이 이미 노선에 등록되어 있다면 에러 반환, 다른 구간이랑 겹칠 때")
    @Test
    void createSectionExceptionWhenAlreadyDownwardStationRegisteredInOtherSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);

        //when
        지하철_구간_등록(1L, 2L, 3L, 7);
        ExtractableResponse<Response> response = 지하철_구간_등록(1L, 3L, 2L, 7);

        //then
        응답코드_검증(response, HttpStatus.OK);
        에러코드_검증(response, ErrorCode.ALREADY_IN_LINE);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 3L, "세번째역");
    }

    /**
     * Given 노선을 생성한다, 구간을 생성한다.
     * When  구간을 삭제한다.
     * Then  노선의 하행선이 기존 구간을 생성하기 전과 동일해진다.
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);
        지하철_구간_등록(1L, 2L, 3L, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 3L);

        //then
        응답코드_검증(response, HttpStatus.NO_CONTENT);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 2L, "두번째역");
    }

    /**
     * Given 노선을 생성한다, 구간을 생성한다.
     * When  노선 하행선 마지막 구간이 아닌 역을 삭제한다.
     * Then  제거 불가능한 에러를 반환한다.
     */
    @DisplayName("지하철 마지막이 아닌 구간을 삭제한다. 제거 불가능한 에러를 반환한다.")
    @Test
    void deleteSectionExceptionWhenNotDownLastStation() {
        //given
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);
        지하철_구간_등록(1L, 2L, 3L, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 2L);

        //then
        응답코드_검증(response, HttpStatus.OK);
        에러코드_검증(response, ErrorCode.CAN_NOT_REMOVE_STATION);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 3L, "세번째역");
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
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 2L);

        //then
        응답코드_검증(response, HttpStatus.OK);


        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 2L, "두번째역");
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
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);
        지하철_구간_등록(1L, 2L, 3L, 7);

        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(2L, 3L);

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
        지하철_노선도_등록("7호선", "bg-1234", 1L, 2L, 5);
        지하철_구간_등록(1L, 2L, 3L, 7);


        //when
        ExtractableResponse<Response> response = 지하철_구간_삭제(1L, 4L);

        //then
        응답코드_검증(response, HttpStatus.OK);
        에러코드_검증(response, ErrorCode.CAN_NOT_REMOVE_STATION);
        LineResponse line7 = 지하철_노선_조회_응답값_반환(1L);
        하행선_기대값_검증(line7, 3L, "세번째역");
    }

    private void 하행선_기대값_검증(LineResponse response, Long stationId, String stationName) {
        StationResponse downwardLastStation = response.getStations().get(1);
        assertAll(
            () -> assertThat(downwardLastStation.getId()).isEqualTo(stationId),
            () -> assertThat(downwardLastStation.getName()).isEqualTo(stationName)
        );
    }
}
