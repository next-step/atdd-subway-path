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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static nextstep.subway.acceptance.step_feature.LineStepFeature.callCreateAndFind;
import static nextstep.subway.acceptance.step_feature.LineStepFeature.*;
import static nextstep.subway.acceptance.step_feature.StationStepFeature.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private StationResponse gangnam;
    private StationResponse yeoksam;
    private StationResponse nonhyeon;
    private StationResponse pangyo;
    private Map<String, String> params;

    @BeforeEach
    void setUpStation() {
        gangnam = StationStepFeature.callCreateAndFind(GANGNAM_STATION_NAME);
        yeoksam = StationStepFeature.callCreateAndFind(YEOKSAM_STATION_NAME);
        nonhyeon = StationStepFeature.callCreateAndFind(NONHYEON_STATION_NAME);
        pangyo = StationStepFeature.callCreateAndFind(PANGYO_STATION_NAME);
        params = LineStepFeature.createLineParams(SHINBUNDANG_LINE_NAME,
                SHINBUNDANG_LINE_COLOR,
                gangnam.getId(),
                yeoksam.getId(),
                10);
    }

    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = LineStepFeature.callCreateLines(params);

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
        LineStepFeature.callCreateLines(params);

        // when
        ExtractableResponse<Response> response = LineStepFeature.callCreateLines(params);

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
        LineAndSectionResponse lineResponse = LineStepFeature.callCreateAndFind(params);
        LineStepFeature.callAddSection(lineResponse.getLineId(), yeoksam.getId(), nonhyeon.getId());

        Map<String, String> number2Line = createLineParams(NUMBER2_LINE_NAME, "green", nonhyeon.getId(), pangyo.getId(), 10);
        LineStepFeature.callCreateLines(number2Line);

        // when
        ExtractableResponse<Response> response = LineStepFeature.callGetLines();

        // then
        LineStepFeature.checkFindLine(response);

        List<LineAndSectionResponse> responses = response.jsonPath()
                .getList(".", LineAndSectionResponse.class);
        LineAndSectionResponse response1 = responses.get(0);
        LineAndSectionResponse response2 = responses.get(1);
        assertThat(response1.getLineName()).isEqualTo(SHINBUNDANG_LINE_NAME);
        assertThat(response2.getLineName()).isEqualTo(NUMBER2_LINE_NAME);
        assertThat(response1.getStations().size()).isEqualTo(3);
        assertThat(response2.getStations().size()).isEqualTo(2);
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
        ExtractableResponse<Response> create = callCreateLines(params);
        String location = create.header("Location");

        // when
        ExtractableResponse<Response> response = LineStepFeature.callGetLines(location);

        // then
        LineStepFeature.checkFindLine(response);

        String lineName = response.jsonPath()
                .getString("name");
        assertThat(lineName).contains(SHINBUNDANG_LINE_NAME);
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
        LineAndSectionResponse createResponse = callCreateAndFind(params);
        Map modifyParams = new HashMap();
        modifyParams.put("id", String.valueOf(createResponse.getLineId()));
        modifyParams.put("name", modifyName);
        modifyParams.put("color", "blue");

        // when
        ExtractableResponse<Response> responseUpdate = LineStepFeature.callUpdateLines(modifyParams);

        // then
        LineStepFeature.checkResponseStatus(responseUpdate.statusCode(), HttpStatus.NO_CONTENT);

        ExtractableResponse<Response> response = LineStepFeature.callGetLines();
        List<String> lineNames = response.jsonPath()
                .getList(".", LineAndSectionResponse.class)
                .stream()
                .map(LineAndSectionResponse::getLineName)
                .collect(toList());
        assertThat(lineNames).contains(modifyName);
        assertThat(lineNames).doesNotContain(SHINBUNDANG_LINE_NAME);
    }

    /**
     * When 없는 지하철 노선의 정보 수정을 요청 하면
     * Then 400 응답
     */
    @DisplayName("지하철 노선 수정 요청 시 노선을 못 찾으면 400응답 처리")
    @Test
    void updateLine_fail() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        params.put("name", "구분당선");
        params.put("color", "blue");

        // when
        ExtractableResponse<Response> response = LineStepFeature.callUpdateLines(params);

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
        LineAndSectionResponse createResponse = callCreateAndFind(params);

        // when
        ExtractableResponse<Response> response = LineStepFeature.callDeleteLines(createResponse.getLineId());

        // then
        LineStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.NO_CONTENT);
    }

    /**
     * Given 지하철 역을 생성한다
     * Given 2개의 역을 이용하여 지하철 노선 생성한다
     * When 새로운 구간의 상행역이 현재 등록되어있는 하행 종점역이며, 새로운 구간의 하행역이 노선에 등록되지 않은 구간을 생성한다
     * Then 지하철 노선 구간 추가가 성공한다
     */
    @DisplayName("노선에 구간 추가")
    @Test
    void addSection() {
        // given
        LineAndSectionResponse lineResponse = LineStepFeature.callCreateAndFind(params);

        // when
        ExtractableResponse<Response> response = LineStepFeature.callAddSection(lineResponse.getLineId(), yeoksam.getId(), nonhyeon.getId());

        // then
        LineStepFeature.checkCreateLine(response);
    }

    /**
     * Given 3개의 역을 이용하여 지하철 노선 생성한다
     * When 마지막 역을 삭제한다
     * Then 구간 삭제 성공
     */
    @DisplayName("노선의 마지막역은 삭제 가능하다")
    @Test
    void deleteSection() {
        // given
        LineAndSectionResponse lineResponse = LineStepFeature.callCreateAndFind(params);
        LineStepFeature.callAddSection(lineResponse.getLineId(), yeoksam.getId(), nonhyeon.getId());

        // when
        ExtractableResponse<Response> response = LineStepFeature.callDeleteSection(lineResponse.getLineId(), nonhyeon.getId());

        // then
        LineStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.NO_CONTENT);
    }

    /**
     * Given 2개의 역을 이용하여 지하철 노선 생성한다
     * When 노선의 구간(마지막 역)을 삭제한다
     * Then 구간 삭제가 실패한다
     */
    @DisplayName("노선에 구간은 최소 1개가 유지되어야 한다")
    @Test
    void deleteSection_minimumSection() {
        // given
        LineAndSectionResponse lineResponse = LineStepFeature.callCreateAndFind(params);

        // when
        ExtractableResponse<Response> response = LineStepFeature.callDeleteSection(lineResponse.getLineId(), yeoksam.getId());

        // then
        LineStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 3개의 역을 이용하여 지하철 노선 생성한다
     * When 마지막 역이 아닌 역을 삭제한다
     * Then 구간 삭제 실패
     */
    @DisplayName("마지막 역만 삭제 가능하다")
    @Test
    void deleteSection_LastDownStation() {
        // given
        LineAndSectionResponse lineResponse = LineStepFeature.callCreateAndFind(params);
        LineStepFeature.callAddSection(lineResponse.getLineId(), yeoksam.getId(), nonhyeon.getId());

        // when
        ExtractableResponse<Response> response = LineStepFeature.callDeleteSection(lineResponse.getLineId(), yeoksam.getId());

        // then
        LineStepFeature.checkResponseStatus(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

}
