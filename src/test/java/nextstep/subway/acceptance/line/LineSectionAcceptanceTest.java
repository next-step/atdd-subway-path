package nextstep.subway.acceptance.line;

import static nextstep.subway.acceptance.line.LineSteps.*;
import static nextstep.subway.acceptance.station.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 노선;
    private Long 상행;
    private Long 하행;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        상행 = 지하철역_생성_요청_하고_ID_반환("상행");
        하행 = 지하철역_생성_요청_하고_ID_반환("하행");

        Map<String, Object> lineCreateParams = LineSteps.createLineCreateParams(상행, 하행);
        노선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * Given 기존 구간의 상행역이 A 이고 하행역이 B 일때
     * When  지하철 노선에 상행역이 B 이고 하행역이 C 인 구간을 추가 하면
     * Then  노선에 새로운 구간이 추가된다
     */
    @DisplayName("노선 마지막 구간 뒤에 새로운 구간 등록")
    @Test
    void addLineSectionCase1() {
        // given
        Long A역 = 상행;
        Long B역 = 하행;

        // when
        Long C역 = 지하철역_생성_요청_하고_ID_반환("C역");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(B역, C역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(A역, B역, C역);
    }

    /**
     * Given 기존 구간의 상행역이 B 이고 하행역이 C 일때
     * When  지하철 노선에 상행역이 A 이고 하행역이 B 인 구간을 추가 하면
     * Then  지하철 노선 맨 처음에 새로운 구간이 추가 된다.
     */
    @DisplayName("노선 처음 구간 앞에 새로운 구간 등록")
    @Test
    void addLineSectionCase2() {
        // given
        Long B역 = 상행;
        Long C역 = 하행;

        // when
        Long A역 = 지하철역_생성_요청_하고_ID_반환("A역");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(A역, B역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(A역, B역, C역);
    }

    /**
     * Given 기존 구간의 상행역이 A 이고 하행역이 C 일때
     * When  지하철 노선에 상행역이 A 이고 하행역이 B 인 구간을 추가 하면
     * Then  노선에 포함된 구간은 (상행)A역-(하행)B역, (상행)B역-(하행)C역으로 변경 된다.
     * And   B역과 C역과의 거리도 다음 계산식의 결과로 변경된다. (B역과 C역의 거리 = A역과 C역의 거리 - A역과 B역의 거리)
     */
    @DisplayName("노선 구간 중간에 새로운 구간 등록")
    @Test
    void addLineSectionCase3() {
        // given
        Long A역 = 상행;
        Long C역 = 하행;
        int beforeLength = 지하철_노선_조회_요청(노선).jsonPath().get("length");

        // when
        Long B역 = 지하철역_생성_요청_하고_ID_반환("A역");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(A역, B역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(A역, B역, C역);
        assertThat((Integer)response.jsonPath().get("length")).isEqualTo(beforeLength);
    }

    /**
     * Given 노선 구간 중간에 새로운 구간을 등록할때
     * When  기존 구간 길이보다 새로운 구간 길이가 큰 구간을 추가 하면
     * Then  구간 추가는 실패 한다.
     */
    @DisplayName("노선 구간 중간에 새로운 구간을 등록할때 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSectionFailCase1() {
        // given
        Long A역 = 상행;

        // when
        Long B역 = 지하철역_생성_요청_하고_ID_반환("A역");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(A역, B역, 999999999));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When  상행과 하행 모두 노선에 이미 추가되어 있는 구간을 추가하면
     * Then  구간 추가는 실패 한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSectionFailCase2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(상행, 하행));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When  상행과 하행 모두 노선에 등록되어 있지 않은 구간을 추가하면
     * Then  구간 추가는 실패 한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있지 않다면 추가할 수 없음")
    @Test
    void addLineSectionFailCase3() {
        // given
        Long 새로운_상행 = 지하철역_생성_요청_하고_ID_반환("새로운 상행");
        Long 새로운_하행 = 지하철역_생성_요청_하고_ID_반환("새로운 하행");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(새로운_상행, 새로운_하행));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 종점 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("노선에 마지막 역을 제거")
    @Test
    void removeLineSectionCase1() {
        // given
        Long 정자역 = 지하철역_생성_요청_하고_ID_반환("정자역");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(하행, 정자역));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(노선, 정자역);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(노선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(상행, 하행);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 종점 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("노선에 첫번째 역을 제거")
    @Test
    void removeLineSectionCase2() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(하행, 정자역));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(노선, 상행);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(노선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(하행, 정자역);
    }

    /**
     * Given (상행)A역-(하행)B역, (상행)B역-(하행)C역 구간이 있을때
     * When  중간 역인 B역을 제거하면
     * Then  노선에 포함된 구간은 (상행)A역-(하행)C역으로 변경 된다.
     * And   A역과 C역과의 거리도 다음 계산식의 결과로 변경된다. (A역과 C역의 거리 = A역과 B역의 거리 + B역과 C역의 거리)
     */
    @DisplayName("노선에 중간 역을 제거")
    @Test
    void removeLineSectionCase3() {
        // given
        Long A역 = 상행;
        Long B역 = 하행;
        Long C역 = 지하철역_생성_요청("C역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(B역, C역));
        int beforeLength = 지하철_노선_조회_요청(노선).jsonPath().get("length");

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(노선, B역);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(노선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(A역, C역);
        assertThat(getResponse.jsonPath().getInt("length")).isEqualTo(beforeLength);
    }

    /**
     * When  노선에 등록되어 있지 않은 지하철 역 제거를 요청할때
     * Then  요청은 실패한다.
     */
    @DisplayName("노선에 등록되어 있지 않은 지하철역 제거 요청")
    @Test
    void removeLineSectionFailCase1() {
        // given
        Long C역 = 지하철역_생성_요청("C역").jsonPath().getLong("id");
        Long 등록되어_있지_않은_지하철_역 = 지하철역_생성_요청("등록되어 있지 않은 지하철 역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(노선, createSectionCreateParams(하행, C역));

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(노선, 등록되어_있지_않은_지하철_역);
        ExtractableResponse<Response> getResponse = 지하철_노선_조회_요청(노선);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(상행, 하행, C역);
    }

    /**
     * When  등록되어 있지 않은 노선으로 지하철 역 제거를 요청할때
     * Then  요청은 실패한다.
     */
    @DisplayName("등록되어 있지 않은 노선으로 지하철 역 삭제 요청")
    @Test
    void removeLineSectionFailCase2() {
        Long 등록되어_있지_않은_노선 = 2L;

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(등록되어_있지_않은_노선, 상행);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 노선에 구간이 1개만 있을때
     * When  구간 삭제를 요청하면
     * Then  요청은 실패한다.
     */
    @DisplayName("구간이 1개만 있을때 지하철 역 삭제 요청")
    @Test
    void removeLineSectionFailCase3() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철_구간_제거_요청(노선, 하행);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
