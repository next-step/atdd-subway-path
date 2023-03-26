package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 정자역;
    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 7);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /*
     * when 역과 역 사이에 새로운 역을 등록하면
     * then 해당 노선을 조회시 추가한 역을 찾을 수 있다.
     * */

    @DisplayName("역 사이에 새로운 역을 등록할 경우 인수테스트")
    @Test
    void betweenAddSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 3));

        // then
        List<Long> idList = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(idList).containsExactly(강남역, 정자역, 양재역)
        );
    }

    /*
    * when 새로운 역을 상행 종점으로 등록하여 구간을 추가할 경우
    * then 새로운 역이 상행 종점역이 되어 노선 조회시 추가한 역을 찾을 수 있다.
    * */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 인수테스트")
    @Test
    void upStationAddSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 3));

        // then
        List<Long> idList = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(idList.get(0)).isEqualTo(정자역)
        );
    }


    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 인수테스트")
    @Test
    void downStationAddSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 3));

        // then
        List<Long> idList = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(idList.get(idList.size()-1)).isEqualTo(정자역)
        );
    }

    /* when 기존 구간 사이에 새로운 구간을 등록할 때, 새로운 구간의 크기가 기존 구간의 크기와 같거나 더 크다면
    *  then 구간 등록에 실패한다.
    * */
    @DisplayName("기존 구간 사이에 구간 등록시, 해당 구간의 크기가 기존 구간 보다 크거나 같다면 등록시 예외가 발생하는 인수테스트")
    @Test
    void addSectionExceptionTest1() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 7));

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /*
    *  when 등록하려는 구간의 상행역과 하행역이 노선에 모두 등록되어 있다면
    *  then 구간 등록에 실패한다.
    * */
    @DisplayName("등록하려는 구간의 상행역, 하행역이 노선에 모두 등록이 되어있다면 등록시 예외가 발생하는 인수테스트")
    @Test
    void addSectionExceptionTest2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /*
    * when 등록하려는 구간의 상행역과 하행역이 노선에 모두 포함되어 있지 않다면
    * then 구간 등록에 실패한다.
    * */
    @DisplayName("등록하려는 구간의 상행역, 하행역이 노선에 모두 포함되어 있지 않다면 등록시 예외가 발생하는 인수테스트")
    @Test
    void addSectionExceptionTest3() {
        // when
        Long 미금역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 3));

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }


    /*
    * given 지하철 노선에 구간을 추가하고
    * when 상행역을 삭제 요청하면
    * then 노선 조회시 삭제한 상행역을 찾을 수 없다.
    * */
    @DisplayName("상행역을 삭제할 경우 인수테스트")
    @Test
    void deleteUpStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        List<Long> idList = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(idList).isNotIn(강남역)
        );

    }

    /*
     * given 지하철 노선에 구간을 추가하고
     * when  하행역을 삭제 요청하면
     * then  노선 조회시 삭제한 하행역을 찾을 수 없다.
     * */
    @DisplayName("하행역을 삭제할 경우 인수테스트")
    @Test
    void deleteDownStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        List<Long> idList = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(idList).isNotIn(정자역)
        );
    }

    /*
     * given 지하철 노선에 구간을 추가하고
     * when  중간역을 삭제 요청하면
     * then  노선 조회시 삭제한 중간역을 찾을 수 없다.
     * */
    @DisplayName("중간역을 삭제할 경우 인수테스트")
    @Test
    void deleteBetweenStation() {
        // given
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역, 5));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        List<Long> idList = 지하철_노선_조회_요청(신분당선).jsonPath().getList("stations.id", Long.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(idList).isNotIn(양재역),
                () -> assertThat(idList).containsExactly(강남역, 정자역, 미금역)
        );

    }

    /*
     * when 노선에 등록되지 않은 역을 제거할 경우
     * then 구간 삭제에 실패한다.
     * */
    @DisplayName("노선에 등록되지 않은 역을 제거할 경우 예외가 발생하는 인수테스트")
    @Test
    void deleteNotExistStationException() {
        // given
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 미금역);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    /*
    * when 노선에 구간이 하나일 경우, 구간 제거 요청을 하면
    * then 구간 삭제에 실패한다.
    * */
    @DisplayName("노선에 구간이 하나 남아있을 경우 구간을 제거 할 경우 예외가 발생하는 인수테스트")
    @Test
    void deleteLastSectionException() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }



    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
