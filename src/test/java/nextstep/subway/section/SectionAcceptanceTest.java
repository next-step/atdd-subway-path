package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.ApiTest;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.section.dto.CreateSectionRequest;
import nextstep.subway.station.StationSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.SubwayLineSteps.*;
import static nextstep.subway.line.SubwayLineSteps.지하철노선등록요청;
import static nextstep.subway.line.SubwayLineSteps.지하철노선등록요청_생성;
import static nextstep.subway.section.SectionSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

class SectionAcceptanceTest extends ApiTest {

    private Long 당고개역, 이수역, 사당역, 당고개역부터_이수역까지의_기존_노선_ID;
    private static final int distance = 10;
    private static final int newDistance = 7;

    @BeforeEach
    void before() {
        // given : 선행조건 기술
        당고개역부터_이수역까지의_기존_노선_ID = 당고개역부터_이수역까지의_기존_노선_생성();
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(당고개 - 상행 종점, 사당역 - 하행 종점)을 등록한다.
     * Then 정상적으로 등록이 되었으면 HttpStatus.OK를 반환한다.
     * </pre>
     */
    @DisplayName("새로운 구간을 등록한다. - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addSection2() {
        // given : 선행조건 기술
        CreateSectionRequest request = 구간등록요청_생성(당고개역, 사당역, newDistance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 동작역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(사당역 - 상행 종점, 당고개역 - 하행 종점)을 등록한다.
     * Then 정상적으로 등록이 되었으면 HttpStatus.OK를 반환한다.
     * </pre>
     */
    @DisplayName("새로운 구간을 등록한다. - 새로운역을 상행 종점으로 등록할 경우")
    @Test
    void addSection3() {
        // given : 선행조건 기술
        CreateSectionRequest request = 구간등록요청_생성(사당역, 당고개역, newDistance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 동작역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(이수역 - 상행 종점, 사당역 - 하행 종점)을 등록한다.
     * Then 정상적으로 등록이 되었으면 HttpStatus.OK를 반환한다.
     * </pre>
     */
    @DisplayName("새로운 구간을 등록한다. - 새로운역을 하행 종점으로 등록할 경우")
    @Test
    void addSection4() {
        // given : 선행조건 기술
        CreateSectionRequest request = 구간등록요청_생성(이수역, 사당역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(당고개 - 상행 종점, 사당역 - 하행 종점)을 등록할때 길이를 기존 노선보다 크게 등록한다.
     * Then 신규 역 사이 길이가 기존 역 사이 길이보다 크면  HttpStatus.BAD_REQUEST 예외를 던진다.
     * </pre>
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 클때 예외 발생")
    @Test
    void addSectionThrowExceptionIsINVALID_DISTANCE() {
        // given : 선행조건 기술
        int distance = 11;
        CreateSectionRequest request = 구간등록요청_생성(당고개역, 사당역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 새로운 구간(당고개 - 상행 종점, 사당역 - 하행 종점)을 등록할때 길이를 기존 노선보다 크게 등록한다.
     * Then 신규 역 사이 길이가 기존 역 사이 길이와 같으면 HttpStatus.BAD_REQUEST 예외를 던진다.
     * </pre>
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이와 같을때 예외 발생")
    @Test
    void addSectionThrowExceptionIsINVALID_DISTANCE2() {
        // given : 선행조건 기술
        CreateSectionRequest request = 구간등록요청_생성(당고개역, 사당역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점, 당고개 - 상행 종점, 사당역 - 하행 종점)으로 구성한다.
     * When 새로운 구간(당고개 - 상행 종점, 사당역 - 하행 종점)을 등록한다.
     * Then 신규 등록하려는 상행역과 하행역이 이미 노선에 등록되어있는 경우 예외를 던진다.
     * </pre>
     */
    @DisplayName("새로운 구간을 등록할 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionThrowExceptionIsALREADY_SECTION() {
        // given : 선행조건 기술
        기존_노선_등록();
        CreateSectionRequest request = 구간등록요청_생성(당고개역, 사당역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.ALREADY_SECTION.getMessage());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성한다.
     * Given 기존 구간 노선은 (당고개역 - 상행 종점, 이수역- 하행 종점, 당고개 - 상행 종점, 사당역 - 하행 종점)으로 구성한다.
     * When 새로운 구간(동작역 - 상행 종점, 이촌역 - 하행 종점)을 등록한다.
     * Then 신규 등록하려는 구간이 상행역과 하행역 둘중 하나도 포함되어 있지 않으면 추가할 수 없다.
     * </pre>
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionThrowExceptionIsCAN_NOT_BE_ADDED_SECTION() {
        // given : 선행조건 기술
        기존_노선_등록();
        Long 동작역 = 역_생성("동작역");
        Long 이촌역 = 역_생성("이촌역");
        CreateSectionRequest request = 구간등록요청_생성(동작역, 이촌역, distance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.CAN_NOT_BE_ADDED_SECTION.getMessage());
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성하고 구간을 두개 등록한다.
     * Given 기존 구간 노선은 1.당고개역 - 상행 종점, 이수역- 하행 종점 길이 10cm
     * Given 2. 이수역 - 상행 종점, 사당역 - 하행 종점 길이 10cm 으로 구성한다.
     * When 노선의 목록(당고개역 - 이수역 - 사당역)에서 이수역을 제거한다.
     * Then 구간을 제거하고 정상적인 요청이면 HttpStatus.OK를 반환한다.
     * Then 구간을 제거하고 목록을 조회하면 (당고개역 - 사당역)이다.
     * Then 구간을 제거하고 목록을 조회하면 길이는 20cm이다.
     * </pre>
     */
    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given : 선행조건 기술
        이수역부터_사당역까지의_신규노선_생성(당고개역부터_이수역까지의_기존_노선_ID);

        // when : 기능 수행
        ExtractableResponse<Response> response = 지하철_노선_구간_제거(당고개역부터_이수역까지의_기존_노선_ID, 이수역);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ExtractableResponse<Response> 노선목록조회응답 = 지하철노선목록조회요청();
        노선_목록은_당고개_사당역이다(노선목록조회응답);
        노선의_길이를_검증한다(노선목록조회응답, 20);
    }

    /**
     * <pre>
     * Given 3개(당고개역, 이수역, 사당역)의 지하철 역을 생성하고 구간을 두개 등록한다.
     * Given 기존 구간 노선은 (1.당고개역 - 상행 종점, 이수역- 하행 종점)으로 구성한다.
     * When 기존 구간(1.당고개역 - 상행 종점, 이수역- 하행 종점)에서 이수역을 제거한다.
     * Then 구간이 하나만 있기 때문에 예외를 반환한다.
     * </pre>
     */
    @DisplayName("구간을 제거할때 제거하려는 노선이 하나만 있으면 예외를 반환한다.")
    @Test
    void removeSectionThrowsExceptionIsSECTION_IS_ONE() {
        // when : 기능 수행
        ExtractableResponse<Response> response = 지하철_노선_구간_제거(당고개역부터_이수역까지의_기존_노선_ID, 이수역);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(ErrorCode.SECTION_IS_ONE.getMessage());
    }

    private Long 당고개역부터_이수역까지의_기존_노선_생성() {
        String stationName1 = "당고개역";
        String stationName2 = "이수역";
        String stationName3 = "사당역";
        String name = "4호선";
        String color = "bg-blue-500";
        당고개역 = 역_생성(stationName1);
        이수역 = 역_생성(stationName2);
        사당역 = 역_생성(stationName3);
        CreateLineRequest 당고개역부터_이수역까지의_기존_노선 = 지하철노선등록요청_생성(name, color, 당고개역, 이수역, distance);
        return 지하철노선등록요청(당고개역부터_이수역까지의_기존_노선).jsonPath().getLong("id");
    }

    private void 이수역부터_사당역까지의_신규노선_생성(Long 당고개역부터_이수역까지의_기존_노선_ID) {
        ExtractableResponse<Response> 이수역부터_사당역까지의_신규구간_생성 = RestAssured.given().log().all()
                .body(구간등록요청_생성(이수역, 사당역, 10))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", 당고개역부터_이수역까지의_기존_노선_ID)
                .then().log().all()
                .extract();

        assertThat(당고개역부터_이수역까지의_기존_노선_ID).isEqualTo(1L);
        assertThat(이수역부터_사당역까지의_신규구간_생성.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static ExtractableResponse<Response> 지하철_노선_구간_제거(Long 당고개역부터_이수역까지의_기존_노선_ID, Long 사당역) {
        return RestAssured.given().log().all()
                .param("stationId", 사당역)
                .when().delete("/lines/{lineId}/sections", 당고개역부터_이수역까지의_기존_노선_ID)
                .then().log().all()
                .extract();
    }
    

    private static Long 역_생성(String stationName1) {
        return StationSteps.지하철생성요청(StationSteps.지하철생성요청_생성(stationName1)).jsonPath().getLong("id");
    }

    private void 기존_노선_등록() {
        // given : 선행조건 기술
        CreateSectionRequest request = 구간등록요청_생성(당고개역, 사당역, newDistance);

        // when : 기능 수행
        ExtractableResponse<Response> response = 구간등록요청(request);

        // then : 결과 확인
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 노선_목록은_당고개_사당역이다(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("stations[0].name", String.class)).hasSize(2)
                .containsExactly("당고개역", "사당역");
        assertThat(response.jsonPath().getList("distance")).containsExactly(20);
    }

    private void 노선의_길이를_검증한다(ExtractableResponse<Response> response, int distance) {
        assertThat(response.jsonPath().getList("distance")).containsExactly(distance);
    }
}
