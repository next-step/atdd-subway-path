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
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.step_feature.LineStepFeature.*;
import static nextstep.subway.acceptance.step_feature.StationStepFeature.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private final int DISTANCE = 100;
    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 정자역;
    private StationResponse 미금역;
    private Map<String, String> params;

    @BeforeEach
    void setUpStation() {
        강남역 = StationStepFeature.지하철역_생성_조회_요청(강남역_이름);
        판교역 = StationStepFeature.지하철역_생성_조회_요청(판교역_이름);
        정자역 = StationStepFeature.지하철역_생성_조회_요청(정자역_이름);
        미금역 = StationStepFeature.지하철역_생성_조회_요청(미금역_이름);
        params = createLineParams(신분당선_이름,
                신분당선_색,
                강남역.getId(),
                정자역.getId(),
                DISTANCE);
    }

    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        LineStepFeature.checkCreateLine(response);
    }

    /**
     * Given 노선을 생성한다.
     * When 같은 이름의 지하철 노선 생성을 요청 하면
     * Then 400 status code를 응답한다.
     */
    @DisplayName("중복된 이름의 지하철 노선 생성은 실패한다")
    @Test
    void createLine_duplicate_fail() {
        // given
        지하철_노선_생성_요청(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        LineStepFeature.checkCreateLineFail(response);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 지하철 노선 생성을 요청 하고
     * When 지하철 노선 목록 조회를 요청 하면
     * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        LineAndSectionResponse lineResponse = 지하철_노선_생성_조회_요청(params);
        지하철_노선에_지하철_구간_생성_요청(lineResponse.getLineId(), 정자역.getId(), 미금역.getId(), DISTANCE);

        Map<String, String> number2Line = createLineParams(이호선_이름, "green", 판교역.getId(), 미금역.getId(), 10);
        지하철_노선_생성_요청(number2Line);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        checkFindLine(response);
        응답받은_노선의_상세_값_확인(response);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선을 응답받는다
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        LineAndSectionResponse createResponse = 지하철_노선_생성_조회_요청(params);

        // when
        ExtractableResponse<Response> response = LineStepFeature.지하철_노선_조회_요청(createResponse.getLineId());

        // then
        checkFindLine(response);

        String lineName = response.jsonPath()
                .getString("name");
        assertThat(lineName).contains(신분당선_이름);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 정보 수정을 요청 하면
     * Then 지하철 노선의 정보 수정은 성공한다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        String modifyName = "구분당선";
        LineAndSectionResponse createResponse = 지하철_노선_생성_조회_요청(params);
        Map<String, String> modifyParams = modifyLineParams(createResponse.getLineId(), modifyName, "blue");

        // when
        ExtractableResponse<Response> responseUpdate = 지하철_노선_수정_요청(modifyParams);

        // then
        LineStepFeature.checkResponseStatus(responseUpdate.statusCode(), HttpStatus.NO_CONTENT);
        노선의_이름_상세_값_확인(modifyName);
    }

    /**
     * When 없는 지하철 노선의 정보 수정을 요청 하면
     * Then 400 응답
     */
    @DisplayName("지하철 노선 수정 요청 시 노선을 못 찾으면 400응답 처리")
    @Test
    void updateLine_fail() {
        // given
        Map<String, String> params = modifyLineParams(1, "구분당선", "blue");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(params);

        // then
        LineStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.NOT_FOUND);
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 삭제를 요청 하면
     * Then 생성한 지하철 노선 삭제가 성공한다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        LineAndSectionResponse createResponse = 지하철_노선_생성_조회_요청(params);

        // when
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(createResponse.getLineId());

        // then
        LineStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.NO_CONTENT);
    }

}
