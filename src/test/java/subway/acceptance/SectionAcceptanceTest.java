package subway.acceptance;

import static subway.acceptance.assertions.LineAssertions.노선_응답_성공_검증;
import static subway.acceptance.assertions.LineAssertions.노선_응답_실패_검증;
import static subway.acceptance.utils.SubwayClient.구간_삭제_요청;
import static subway.acceptance.utils.SubwayClient.구간_생성_요청;
import static subway.acceptance.utils.SubwayClient.노선_생성_요청;
import static subway.acceptance.utils.SubwayClient.노선_조회_요청;
import static subway.acceptance.utils.SubwayClient.지하철역_생성_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.exception.error.SubwayErrorCode;


@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    Long 강남역;
    Long 광교역;
    Long 양재역;
    Long 논현역;
    Long 신논현역;
    Long 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = 지하철역_생성_요청(new StationRequest("강남역")).jsonPath().getLong("id");
        광교역 = 지하철역_생성_요청(new StationRequest("광교역")).jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청(new StationRequest("양재역")).jsonPath().getLong("id");
        논현역 = 지하철역_생성_요청(new StationRequest("논현역")).jsonPath().getLong("id");
        신논현역 = 지하철역_생성_요청(new StationRequest("신논현역")).jsonPath().getLong("id");

        신분당선 = 노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 광교역, 논현역, 30L)).jsonPath().getLong("id");
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 새로운 구간을 등록한다.")
    void 새로운_구간_등록() {
        // When
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(논현역, 신논현역, 20L));

        // Then
        노선_응답_성공_검증(구간_생성_응답, HttpStatus.CREATED, 50L, List.of(광교역, 논현역, 신논현역));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 새로운 역을 상행역 기준으로 기존 구간 사이에 등록한다.")
    void 새로운_역을_상행역_기준으로_기존_구간_사이에_등록() {
        // When
        // 노선은 상행(광교역) 하행(논현역) 으로 종점이 등록되어있는 상태이며,
        // 새로운 지하철역(양재역)을 (*광교역-*양재역-논현역) 으로 등록하고자 함
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(광교역, 양재역, 7L));

        // Then
        노선_응답_성공_검증(구간_생성_응답, HttpStatus.CREATED, 30L, List.of(광교역, 양재역, 논현역));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 새로운 역을 하행역 기준으로 기존 구간 사이에 등록한다.")
    void 새로운_역을_하행역_기준으로_기존_구간_사이에_등록() {
        // When
        // 노선은 상행(광교역) 하행(논현역) 으로 종점이 등록되어있는 상태이며,
        // 새로운 지하철역(양재역)을 (광교역-*양재역-*논현역) 으로 등록하고자 함
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(양재역, 논현역, 7L));

        // Then
        노선_응답_성공_검증(구간_생성_응답, HttpStatus.CREATED, 30L, List.of(광교역, 양재역, 논현역));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 새로운 역을 상행종점역으로 등록한다.")
    void 새로운_역을_상행종점역으로_등록() {
        // When
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(강남역, 광교역, 7L));

        // Then
        노선_응답_성공_검증(구간_생성_응답, HttpStatus.CREATED, 37L, List.of(강남역, 광교역, 논현역));
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 역 사이 길이보다 긴 구간을 추가한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[실패] 기존 역 사이의 길이보다 큰 새로운 역을 등록한다.")
    void 기존_역_사이의_길이보다_큰_새로운_역을_등록() {
        // When
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(광교역, 양재역, 40L));

        // Then
        노선_응답_실패_검증(구간_생성_응답, SubwayErrorCode.CANNOT_CREATE_SECTION);
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 역 사이 길이와 같은 구간을 추가한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[실패] 기존 역 사이의 길이와 같은 새로운 역을 등록한다.")
    void 기존_역_사이의_길이와_같은_새로운_역을_등록() {
        // When
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(광교역, 양재역, 30L));

        // Then
        노선_응답_실패_검증(구간_생성_응답, SubwayErrorCode.CANNOT_CREATE_SECTION);
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 구간과 같은 구간을 등록한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[실패] 상하행종점역 둘 다 이미 등록된 역을 등록한다.")
    void 상하행종점역_둘_다_이미_등록된_역을_등록() {
        // When
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(광교역, 논현역, 30L));

        // Then
        노선_응답_실패_검증(구간_생성_응답, SubwayErrorCode.CANNOT_CREATE_SECTION);
    }


    /**
     * Scenario: 상하행종점역 둘 중 하나도 포함되어 있지 않은 역을 등록한다.
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 2개의 구간이 등록되어 있다.
     * When: 상하행종점역이 기존 구간 어디에도 포함되어 있지 않은 구간을 등록한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[실패] 상하행종점역 둘 중 하나도 포함되어 있지 않은 역을 등록한다.")
    void 상하행종점역_둘_중_하나도_포함되어_있지_않은_역_등록() {
        // When
        ExtractableResponse<Response> 구간_생성_응답 = 구간_생성_요청(신분당선, new SectionRequest(0L, 100L, 30L));

        // Then
        노선_응답_실패_검증(구간_생성_응답, SubwayErrorCode.STATION_NOT_FOUND);
    }


    /**
     * Given: 5개의 지하철역을 등록한다.
     * And: 1개의 노선을 등록한다.
     * And: 3개의 구간을 등록한다.
     * When: 노선을 조회한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 지하철역 아이디 리스트의 순서를 검증한다.
     */
    @Test
    @DisplayName("[성공] 구간을 조회한다.")
    public void 구간을_조회() {
        // Given
        구간_생성_요청(신분당선, new SectionRequest(광교역, 양재역, 7L));
        구간_생성_요청(신분당선, new SectionRequest(강남역, 광교역, 10L));
        구간_생성_요청(신분당선, new SectionRequest(논현역, 신논현역, 10L));

        // When
        ExtractableResponse<Response> 구간_조회_응답 = 노선_조회_요청(신분당선);

        // Then
        노선_응답_성공_검증(구간_조회_응답, HttpStatus.OK, 50L, List.of(강남역, 광교역, 양재역, 논현역, 신논현역));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * And: 2개의 구간이 등록되어 있다.
     * When: 중간 구간을 제거한다.
     * Then: 성공(204 NO CONTENT) 응답을 받는다.
     * And: 구간을 조회한다.
     * And: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 중간 구간을 제거한다.")
    void 중간_구간을_제거() {
        // Given
        구간_생성_요청(신분당선, new SectionRequest(양재역, 논현역, 20L));

        // When
        구간_삭제_요청(신분당선, 양재역);

        // Then
        ExtractableResponse<Response> 구간_조회_응답 = 노선_조회_요청(신분당선);
        노선_응답_성공_검증(구간_조회_응답, HttpStatus.OK, 30L, List.of(광교역, 논현역));
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 지하철 노선이 등록되어 있다.
     * And: 2개의 구간이 등록되어 있다.
     * When: 첫 번째 구간(상행 종점역)을 삭제한다.
     * Then: 성공(204 NO CONTENT) 응답을 받는다.
     * And: 구간을 조회한다.
     * And: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 첫 번째 구간을 삭제한다.")
    void 첫_번째_구간을_삭제() {
        // Given
        구간_생성_요청(신분당선, new SectionRequest(양재역, 논현역, 20L));

        // When
        구간_삭제_요청(신분당선, 광교역);

        // Then
        ExtractableResponse<Response> 구간_조회_응답 = 노선_조회_요청(신분당선);
        노선_응답_성공_검증(구간_조회_응답, HttpStatus.OK, 20L, List.of(양재역, 논현역));
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 지하철 노선이 등록되어 있다.
     * And: 2개의 구간이 등록되어 있다.
     * When: 구간을 제거한다.
     * Then: 성공(204 NO CONTENT) 응답을 받는다.
     * And: 구간을 조회한다.
     * And: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[성공] 마지막 구간을 삭제한다.")
    void 마지막_구간을_삭제() {
        // Given
        구간_생성_요청(신분당선, new SectionRequest(논현역, 신논현역, 20L));

        // When
        구간_삭제_요청(신분당선, 신논현역);

        // Then
        ExtractableResponse<Response> 구간_조회_응답 = 노선_조회_요청(신분당선);
        노선_응답_성공_검증(구간_조회_응답, HttpStatus.OK, 30L, List.of(광교역, 논현역));
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * And: 1개의 구간이 등록되어 있다.
     * When: 구간을 제거한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 삭제할 수 없습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("[실패] 단일 구간으로 이루어진 노선에서 구간을 삭제한다.")
    void 단일_구간으로_이루어진_노선에서_구간을_삭제() {
        // When
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(신분당선, 신논현역);

        // Then
        노선_응답_실패_검증(구간_삭제_응답, SubwayErrorCode.CANNOT_DELETE_SECTION);

    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * When: 노선에 등록되어있지 않은 역을 제거한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 삭제할 수 없습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("[실패] 노선에 등록되어있지 않은 역을 제거한다.")
    void 노선에_등록되어있지_않은_역을_제거() {
        // When
        ExtractableResponse<Response> 구간_삭제_응답 = 구간_삭제_요청(신분당선, 100L);

        // Then
        노선_응답_실패_검증(구간_삭제_응답, SubwayErrorCode.STATION_NOT_FOUND);
    }

}
