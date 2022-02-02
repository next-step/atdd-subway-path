package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step_feature.LineStepFeature;
import nextstep.subway.acceptance.step_feature.StationStepFeature;
import nextstep.subway.applicaion.dto.LineAndSectionResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.step_feature.LineStepFeature.*;
import static nextstep.subway.acceptance.step_feature.StationStepFeature.*;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

    private final int DISTANCE = 100;
    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 정자역;
    private StationResponse 미금역;
    private Map<String, String> params;
    LineAndSectionResponse 신분당선;

    @BeforeEach
    void setUpStation() {
        강남역 = StationStepFeature.지하철역_생성_조회_요청(강남역_이름);
        판교역 = StationStepFeature.지하철역_생성_조회_요청(판교역_이름);
        정자역 = StationStepFeature.지하철역_생성_조회_요청(정자역_이름);
        미금역 = StationStepFeature.지하철역_생성_조회_요청(미금역_이름);
        params = LineStepFeature.노선_생성_Param_생성(신분당선_이름,
                신분당선_색,
                강남역.getId(),
                정자역.getId(),
                DISTANCE);
        신분당선 = 지하철_노선_생성_조회_요청(params);
    }

    /**
     * Given 지하철 역을 생성한다
     * When 새로운 역을 상행 종점으로 등록한다
     * Then 구간 추가 성공 응답을 받는다
     */
    @DisplayName("새로운 역을 기존 구간의 중간에 등록할 경우")
    @Test
    void addSection_middleStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 강남역.getId(), 판교역.getId(), 50);

        // then
        노선_생성_응답상태_검증(response);
    }

    /**
     * Given 지하철 역을 생성한다
     * When 새로운 역을 상행 종점으로 등록한다
     * Then 구간 추가 성공 응답을 받는다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_upStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 판교역.getId(), 강남역.getId(), DISTANCE);

        // then
        노선_생성_응답상태_검증(response);
    }

    /**
     * Given 지하철 역을 생성한다
     * When 새로운 역을 하행 종점으로 등록한다
     * Then 구간 추가 성공 응답을 받는다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_downStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 정자역.getId(), 미금역.getId(), DISTANCE);

        // then
        노선_생성_응답상태_검증(response);
    }

    /**
     * Given 지하철 역을 생성한다
     * When 기존 구간과 길이가 같은 구간을 추가한다
     * Then 구간 추가 실패 응답을 받는다
     */
    @DisplayName("새로운 구간을 중간에 추가할 경우, 길이가 기존 구간의 길이와 같거나 크면, 구간 추가를 실패한다")
    @ValueSource(ints = {100, 120, 500, 1000})
    @ParameterizedTest
    void addSection_distance_fail(int distance) {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 강남역.getId(), 판교역.getId(), distance);

        // then
        노선_생성_실패_응답상태_검증(response);
    }

    /**
     * Given 지하철 역을 생성한다
     * When 기존 구간에 등록된 역들을 새로 추가 한다
     * Then 구간 추가 실패 응답을 받는다
     */
    @DisplayName("기존 등록된 역을 상행, 하행역으로 가지는 구간은 추가 실패한다")
    @Test
    void addSection_station_registered_fail() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 정자역.getId(), 강남역.getId(), 50);

        // then
        노선_생성_실패_응답상태_검증(response);
    }

    /**
     * Given 지하철 역을 생성한다
     * When 기존 구간에 등록되지 않은 역을 가지는 구간을 새로 추가 한다
     * Then 구간 추가 실패 응답을 받는다
     */
    @DisplayName("기존 등록된 역을 상행 또는 하행역으로 가지지 않은 구간은 추가 실패한다")
    @Test
    void addSection_connect_station_fail() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 판교역.getId(), 미금역.getId(), 50);

        // then
        노선_생성_실패_응답상태_검증(response);
    }

    /**
     * Given 3개의 역을 이용하여 지하철 노선 생성한다
     * When 첫 번째 역을 삭제한다
     * Then 삭제 성공 응답을 받는다
     */
    @DisplayName("노선의 첫 번째 역을 삭제할 수 있다")
    @Test
    void deleteSection_firstStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 정자역.getId(), 미금역.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선.getLineId(), 미금역.getId());

        // then
        노선_삭제_응답상태_검증(response);
    }


    /**
     * Given 3개의 역을 이용하여 지하철 노선 생성한다
     * When 마지막 역을 삭제한다
     * Then 구간 삭제 성공 응답을 받는다
     */
    @DisplayName("노선의 마지막 역을 삭제할 수 있다")
    @Test
    void deleteSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 정자역.getId(), 미금역.getId(), DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선.getLineId(), 미금역.getId());

        // then
        노선_삭제_응답상태_검증(response);
    }

    /**
     * Given 3개의 역을 이용하여 지하철 노선 생성한다
     * When 노선의 중간 역을 삭제한다
     * Then 구간 삭제 응답을 받는다
     */
    @DisplayName("노선의 중간 역을 삭제할 수 있다")
    @Test
    void deleteSection_LastDownStation() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 정자역.getId(), 미금역.getId(), 50);
        지하철_노선에_지하철_구간_생성_요청(신분당선.getLineId(), 판교역.getId(), 정자역.getId(), 50);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선.getLineId(), 판교역.getId());

        // then
        노선_삭제_응답상태_검증(response);
    }

    /**
     * Given 2개의 역을 이용하여 지하철 노선 생성한다
     * When 노선의 구간(마지막 역)을 삭제한다
     * Then 구간 삭제 실패 응답을 받는다
     */
    @DisplayName("노선에 구간은 최소 1개가 유지되어야 한다")
    @Test
    void deleteSection_minimumSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선.getLineId(), 판교역.getId());

        // then
        노선_응답_상태코드_검증(response.statusCode(), HttpStatus.BAD_REQUEST);
    }


}
