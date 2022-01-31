package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_역으로_구간_삭제_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선을_수정한다;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청한다;
import static nextstep.subway.common.LineSomething.DISTANCE_BASIC;
import static nextstep.subway.common.LineSomething.DISTANCE_INVALID;
import static nextstep.subway.common.LineSomething.DISTANCE_VALID;
import static nextstep.subway.utils.RestAssuredCRUD.응답결과가_BAD_REQUEST;
import static nextstep.subway.utils.RestAssuredCRUD.응답결과가_CREATED;
import static nextstep.subway.utils.RestAssuredCRUD.응답결과가_NO_CONTENT;
import static nextstep.subway.utils.RestAssuredCRUD.응답결과가_OK;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.utils.RestAssuredCRUD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 일번역1;
    private Long 이번역2;
    private Long 삼번역3;
    private Long 사번역4;
    private Long 신규역;

    private ExtractableResponse<Response> response;
    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        일번역1 = 지하철역_생성_요청한다(StationRequest.of("일번역1")).as(StationResponse.class).getId();
        이번역2 = 지하철역_생성_요청한다(StationRequest.of("이번역2")).as(StationResponse.class).getId();
        삼번역3 = 지하철역_생성_요청한다(StationRequest.of("삼번역3")).as(StationResponse.class).getId();
        사번역4 = 지하철역_생성_요청한다(StationRequest.of("사번역4")).as(StationResponse.class).getId();

        response = 지하철_노선_생성_요청(LineRequest.of("신분당선", "bg-red-600",
            일번역1, 이번역2, DISTANCE_BASIC));
        응답결과가_CREATED(response);
        신분당선 = response.jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(이번역2, 삼번역3, DISTANCE_BASIC));
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(삼번역3, 사번역4, DISTANCE_BASIC));

        신규역 = 지하철역_생성_요청한다(StationRequest.of("신규역")).as(StationResponse.class).getId();
    }

    @DisplayName("지하철 노선에서 (최하행) 역 아이디를 이용해서 구간을 삭제 성공 한다")
    @Test
    void deleteSection_성공케이스_1_해피케이스() {
        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 사번역4);
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 삼번역3));
    }

    @DisplayName("지하철 노선에서 (중간역) 역 아이디를 이용해서 구간을 삭제 성공 한다")
    @Test
    void deleteSection_성공케이스_2() {
        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 삼번역3);
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 사번역4));
    }

    @DisplayName("지하철 노선에서 (상행) 역 아이디를 이용해서 구간을 삭제 성공 한다")
    @Test
    void deleteSection_성공케이스_3() {
        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 일번역1);
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(이번역2, 삼번역3, 사번역4));
    }

    @DisplayName("지하철 노선에서 역 아이디를 이용해서 구간을 삭제하는데, 노선이 없으면 실패한다")
    @Test
    void deleteSection_실패케이스_1() {
        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 일번역1);

        // then
        응답결과가_BAD_REQUEST(response);
    }

    @DisplayName("지하철 노선에서 역 아이디를 이용해서 구간을 삭제하는데, 역 없으면 실패한다")
    @Test
    void deleteSection_실패케이스_2() {
        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 일번역1);

        // then
        응답결과가_BAD_REQUEST(response);
    }

    @DisplayName("지하철 노선에서 역 아이디를 이용해서 구간을 삭제하는데, 구간이 1개면 실패한다")
    @Test
    void deleteSection_실패케이스_3() {
        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 사번역4);
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 삼번역3);
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선에_지하철_역으로_구간_삭제_요청(신분당선, 이번역2);
        // then
        응답결과가_BAD_REQUEST(response);
    }

    @DisplayName("기존 지하철 노선 뒤에 구간 추가 성공하는 단순 케이스 (해피케이스)")
    @Test
    void addLineSection_성공케이스_해피케이스() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(사번역4, 신규역, DISTANCE_VALID));
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        응답결과가_OK(response);
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 삼번역3, 사번역4, 신규역));
    }

    @DisplayName("지하철 노선에 상행선쪽에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_1() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(신규역, 일번역1, DISTANCE_VALID));
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        응답결과가_OK(response);
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(신규역, 일번역1, 이번역2, 삼번역3, 사번역4));
    }

    @DisplayName("지하철 노선에 있는 구간들 중 상행역 쪽 중간역에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_2() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(이번역2, 신규역, DISTANCE_VALID));
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        응답결과가_OK(response);
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 신규역, 삼번역3, 사번역4));
    }

    @DisplayName("지하철 노선에 있는 구간들 중 하행역 쪽 중간역에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_3() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(신규역, 삼번역3, DISTANCE_VALID));
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        응답결과가_OK(response);
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 신규역, 삼번역3, 사번역4));
    }

    @DisplayName("지하철 노선에 하행선쪽에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_4() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(사번역4, 신규역, DISTANCE_VALID));
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        응답결과가_OK(response);
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 삼번역3, 사번역4, 신규역));
    }

    @DisplayName("지하철 노선에 있는 구간 사이에 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 실패")
    @Test
    void addLineSection_실패케이스_1() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
            SectionRequest.of(이번역2, 신규역, DISTANCE_INVALID));

        // then
        응답결과가_BAD_REQUEST(response);
    }

    @DisplayName("지하철 노선에 구간을 등록 시 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록 실패")
    @Test
    void addLineSection_실패케이스_2() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
            SectionRequest.of(신규역, 신규역, DISTANCE_VALID));

        // then
        응답결과가_BAD_REQUEST(response);
    }

    @DisplayName("지하철 노선에 구간을 등록 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록 실패")
    @Test
    void addLineSection_실패케이스_3() {
        // when
        response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
            SectionRequest.of(일번역1, 사번역4, DISTANCE_VALID));

        // then
        응답결과가_BAD_REQUEST(response);
    }

    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(사번역4, 신규역, DISTANCE_VALID));

        // when
        response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 신규역);
        // then
        응답결과가_OK(response);

        // when
        response = 지하철_노선_조회_요청(신분당선);
        // then
        응답결과가_OK(response);
        노선_구간의_역들이_기대하는_순서대로_등록_확인된다(response, Arrays.asList(일번역1, 이번역2, 삼번역3, 사번역4));
    }

    @DisplayName("지하철 노선 정보를 수정 성공한다")
    @Test
    void updateLineSection() {
        // when
        LineRequest lineRequest = LineRequest.of("수정이름", "수정색상");
        response = 지하철_노선을_수정한다(신분당선, lineRequest);
        // then
        응답결과가_OK(response);

        // then
        response = 지하철_노선_조회_요청(신분당선);
        응답결과가_OK(response);
        노선이_수정되어있다(response, lineRequest);
    }

    private void 노선_구간의_역들이_기대하는_순서대로_등록_확인된다(ExtractableResponse response, List<Long> ids) {
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactlyElementsOf(ids);
    }

    private void 노선이_수정되어있다(ExtractableResponse response, LineRequest lineRequest) {
        assertThat(response.as(LineResponse.class).getName()).isEqualTo(lineRequest.getName());
        assertThat(response.as(LineResponse.class).getColor()).isEqualTo(lineRequest.getColor());
    }
}
