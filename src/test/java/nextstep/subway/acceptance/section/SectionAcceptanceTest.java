package nextstep.subway.acceptance.section;

import static nextstep.subway.acceptance.AcceptanceTestBase.assertStatusCode;
import static nextstep.subway.acceptance.ResponseParser.*;
import static nextstep.subway.acceptance.line.LineAcceptanceTestHelper.*;
import static nextstep.subway.acceptance.section.SectionAcceptanceTestHelper.*;
import static nextstep.subway.acceptance.station.StationAcceptanceTestHelper.지하철_파라미터_생성;
import static nextstep.subway.acceptance.station.StationAcceptanceTestHelper.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@AcceptanceTest
public class SectionAcceptanceTest {
    final String 상행역 = "강남역";
    private String 상행ID;
    final String 하행역 = "역삼역";
    private String 하행ID;
    final String 신규역 = "선릉역";
    private Long 노선ID;

    @BeforeEach
    void setup() {
        상행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(상행역)));
        하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(하행역)));

        노선ID = getIdFromResponse(노선_생성_요청(노선_파라미터_생성("2호선", 상행ID, 하행ID)));

    }

    @Test
    @DisplayName("구간 추가시 노선 처음에 추가할 수 있다.")
    void AddFrontSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));

        //when
        var response = 구간_등록_요청(구간_파라미터_생성(하행ID, 신규하행ID), 노선ID);

        // then
        assertStatusCode(response, CREATED);
    }
    @Test
    @DisplayName("구간 추가시 노선 중간에 추가할 수 있다.")
    void AddMidSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));

        //when
        var response = 구간_등록_요청(구간_파라미터_생성(하행ID, 신규하행ID), 노선ID);

        // then
        assertStatusCode(response, CREATED);
    }
    @Test
    @DisplayName("구간 추가시 노선 마지막에 추가할 수 있다.")
    void AddLastSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));
        //when
        var response = 구간_등록_요청(구간_파라미터_생성(하행ID, 신규하행ID), 노선ID);

        // then
        assertStatusCode(response, CREATED);
    }
    @Test
    @DisplayName("구간 추가시 이미 등록된 역은 추가할 수 없다.")
    void AlreadyRegisterSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));
        구간_등록_요청(구간_파라미터_생성(하행ID, 신규하행ID), 노선ID);

        //when
        var response = 구간_등록_요청(구간_파라미터_생성(신규하행ID, 상행ID), 노선ID);

        // then
        assertStatusCode(response, BAD_REQUEST);
        assertThat(노선_하행ID조회(노선ID)).isEqualTo(신규하행ID);
    }


    @Test
    @DisplayName("구간 제거시 노선 처음에 제거할 수 있다.")
    void deleteFrontSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));
        구간_등록_요청(구간_파라미터_생성(신규하행ID, 상행ID), 노선ID);

        //when
        var response = 구간_제거_요청(신규하행ID, 노선ID);

        // then
        assertStatusCode(response, NO_CONTENT);
    }
    @Test
    @DisplayName("구간 제거시 노선 중간에 제거할 수 있다.")
    void deleteMidSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));
        구간_등록_요청(구간_파라미터_생성(상행ID, 신규하행ID), 노선ID);

        //when
        var response = 구간_제거_요청(신규하행ID, 노선ID);

        // then
        assertStatusCode(response, NO_CONTENT);
    }
    @Test
    @DisplayName("구간 제거시 노선 마지막에 제거할 수 있다.")
    void deleteLastSectionAcceptanceTest() {
        //given
        String 신규하행ID = getStringIdFromResponse(지하철역_생성_요청(지하철_파라미터_생성(신규역)));
        구간_등록_요청(구간_파라미터_생성(하행ID, 신규하행ID), 노선ID);

        //when
        var response = 구간_제거_요청(신규하행ID, 노선ID);

        // then
        assertStatusCode(response, NO_CONTENT);
    }
}
