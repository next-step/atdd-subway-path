package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.fixture.StationFixtures;
import nextstep.subway.global.error.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.DistanceFixtures.*;
import static nextstep.subway.fixture.LineFixtures.BACKGROUND_COLOR_BLUE;
import static nextstep.subway.fixture.LineFixtures.SHINBUNDANG_LINE;
import static nextstep.subway.fixture.PathFixtures.*;
import static nextstep.subway.fixture.StationFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;
    private Long 강남역;
    private Long 양재역;

    /**
     * given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성_요청(GANGNAM_STATION).jsonPath().getLong(PATH_ID);
        양재역 = 지하철역_생성_요청(YANGJAE_STATION).jsonPath().getLong(PATH_ID);

        Map<String, String> lineCreateParams = createLineCreateParams(SHINBUNDANG_LINE, BACKGROUND_COLOR_BLUE, 강남역, 양재역, DISTANCE_TEN);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong(PATH_ID);
    }

    /**
     * given 지하철역 생성을 요청 하고
     * when 지하철 노선의 마지막 구간의 하행역이 추가 되는 새로운 구간의 상행역과 동일하다면
     * then 노선에 마지막 부분에 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선의_마지막_부분에_구간을_추가한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(YANGJAE_STATION).jsonPath().getLong(PATH_ID);

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_FIVE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * given 지하철역 생성을 요청 하고
     * when 지하철 노선의 시작 구간의 상행역이 추가 되는 새로운 구간의 하행역과 동일하다면
     * then 노선의 시작 부분에 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선의_시작_부분에_구간을_추가한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, DISTANCE_FIVE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(정자역, 강남역, 양재역);
    }


    /**
     * given 지하철역 생성을 요청 하고
     * when 지하철 노선의 구간의 상행역과 추가 되는 새로운 구간의 상행역과 동일하고
     * when 새로운 구간의 길이가 더 짧을 때
     * then 이전의 구간을 삭제하고 그 자리에 두 개로 나뉘어진 새로운 구간이 추가된다
     */
    @Test
    void 지하철_노선의_중간에_구간을_추가한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);

        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, DISTANCE_FIVE));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    /**
     * given 지하철역 생성을 요청 하고
     * when 기존 구간에 포함된 상행역에서 출발하는 새로운 구간을 추가하고
     * when 새로운 구간의 거리는 기존 구간의 거리보다 길거나 같으면
     * then INVALID_VALUE_EXCEPTION_ERROR -
     * then NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH 에러가 발생한다
     */
    @Test
    void 추가하는_구간이_기존의_구간의_역_사이의_길이보다_크거나_같으면_예외가_발생한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, DISTANCE_TWENTY));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getList(PATH_ERROR_MESSAGES, String.class)).containsExactly(ErrorCode.NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH.getErrorMessage());
    }

    /**
     * given 지하철역과 구간 생성을 요청 하고
     * when 추가할 노선의 상행역과 하행역이 이미 노선에 모두 등록되어 있다면
     * then INVALID_VALUE_EXCEPTION_ERROR -
     * then NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH 에러가 발생한다
     */
    @Test
    void 추가할_구간의_상행역과_하행역이_이미_등록되어_있는_경우_예외가_발생한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_TWENTY));

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 강남역, DISTANCE_FIVE));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getList(PATH_ERROR_MESSAGES, String.class)).containsExactly(ErrorCode.ALREADY_EXISTED_STATIONS_OF_NEW_SECTION.getErrorMessage());
    }

    /**
     * given 지하철역 생성을 요청 하고
     * when 추가할 노선의 상행역과 하행역이 하나도 노선에 포함되어 있지 않다면
     * then INVALID_VALUE_EXCEPTION_ERROR -
     * then NOT_EXISTS_STATIONS_OF_NEW_SECTION 에러가 발생한다
     */
    @Test
    void 추가할_구간의_상행역과_하행역이_모두_노선에_포함되어_있지_않는_경우_예외가_발생한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        Long 송파역 = 지하철역_생성_요청(StationFixtures.SONGPA_STATION).jsonPath().getLong(PATH_ID);

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 송파역, DISTANCE_TWENTY));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getList(PATH_ERROR_MESSAGES, String.class)).containsExactly(ErrorCode.NOT_EXISTS_STATIONS_OF_SECTION.getErrorMessage());
    }

    /**
     * given 지하철역과 구간 생성을 요청 하고
     * when 지하철 노선의 마지막 구간에 위치한 지하철 역을 제거하면
     * then 노선에 구간과 함께 지하철 역이 제거된다
     */
    @Test
    void 지하철_노선의_마지막_구간의_지하철_역을_삭제한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_FIVE));

        //when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * given 지하철역과 구간 생성을 요청 하고
     * when 지하철 노선의 첫 번째 구간에 위치한 지하철 역을 제거하면
     * then 노선에 구간과 함께 지하철 역이 제거된다
     */
    @Test
    void 지하철_노선의_처음_구간의_지하철_역을_삭제한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_FIVE));

        //when
        ExtractableResponse<Response> sectionResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(신분당선);

        //then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        Assertions.assertThat(lineResponse.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * given 지하철역과 구간 생성을 요청 하고
     * when 지하철 노선의 중간에 위치한 구간에 지하철 역을 제거하면
     * then 지하철 역을 포함 두 구간이 삭제 되고 두 구간의 상행역, 하행역을 잇는 새로운 구간이 노선에 추가된다.
     */
    @Test
    void 지하철_노선의_중간_구간의_지하철_역을_삭제한다() {
        //given
        Long 정자역 = 지하철역_생성_요청(JUNGJA_STATION).jsonPath().getLong(PATH_ID);
        Long 송파역 = 지하철역_생성_요청(SONGPA_STATION).jsonPath().getLong(PATH_ID);
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, DISTANCE_FIVE));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 송파역, DISTANCE_FIVE));

        //when
        ExtractableResponse<Response> sectionResponse = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(신분당선);

        //then
        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        Assertions.assertThat(lineResponse.jsonPath().getList(PATH_STATIONS_ID, Long.class)).containsExactly(강남역, 정자역, 송파역);
    }

    /**
     * when 지하철 노선의 한 개 밖에 없는 구간에 지하철 역을 제거하면
     * then 삭제되지 않고 예외가 발생한다.
     */
    @Test
    void 지하철_노선의_구간이_한_개라면_삭제할_수_없다() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getList(PATH_ERROR_MESSAGES, String.class)).containsExactly(ErrorCode.CAN_NOT_REMOVE_ONLY_ONE_SECTION.getErrorMessage());
    }
}
