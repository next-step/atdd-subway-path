package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.dto.LineTestRequest;
import nextstep.subway.acceptance.dto.SectionTestRequest;
import nextstep.subway.acceptance.step.LineTestStep;
import nextstep.subway.acceptance.step.SectionTestStep;
import nextstep.subway.acceptance.step.StationTestStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private LineTestRequest lineRequest;
    private Long lineId;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        lineRequest = LineTestStep.지하철_노선_요청_신분당선_데이터_생성하기();
        lineId = LineTestStep.지하철_노선_생성한_후_아이디_추출하기(lineRequest);
    }

    @DisplayName("구간 등록 기능")
    @Test
    void createSection() {
        // given
        Long 양재시민의숲역_id = StationTestStep.지하철역_생성_후_아이디_추출하기("양재시민의숲역");
        SectionTestRequest sectionRequest = new SectionTestRequest(lineRequest.getDownStationId(), 양재시민의숲역_id, 3);

        // when
        ExtractableResponse<Response> response = SectionTestStep.지하철역_구간_생성하기(sectionRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("구간 등록 시 상행역이 노선의 하행 종점역이 아닐 때 실패 기능")
    @Test
    void createSectionNotDownStationFail() {
        // given
        Long 양재시민의숲역_id = StationTestStep.지하철역_생성_후_아이디_추출하기("양재시민의숲역");
        SectionTestRequest sectionRequest = new SectionTestRequest(양재시민의숲역_id, lineRequest.getDownStationId(), 3);

        // when
        ExtractableResponse<Response> response = SectionTestStep.지하철역_구간_생성하기(sectionRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("구간 등록 시 하행역이 해당 노선에 등록되어 있을 때 실패 기능")
    @Test
    void createSectionDuplicateDownStationFail() {
        // given
        SectionTestRequest sectionRequest = new SectionTestRequest(
                lineRequest.getDownStationId(), lineRequest.getUpStationId(), 3);

        // when
        ExtractableResponse<Response> response = SectionTestStep.지하철역_구간_생성하기(sectionRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("구간 제거 기능")
    @Test
    void deleteSection() {
        // given
        Long 양재시민의숲역_id = StationTestStep.지하철역_생성_후_아이디_추출하기("양재시민의숲역");
        SectionTestRequest sectionRequest = new SectionTestRequest(lineRequest.getDownStationId(), 양재시민의숲역_id, 3);
        SectionTestStep.지하철역_구간_생성하기(sectionRequest, lineId);

        // when
        ExtractableResponse<Response> response = SectionTestStep.지하철역_구간_삭제하기(lineId, 양재시민의숲역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간 제거 시 하행 종점역이 아닌 역을 요청했을 경우 실패")
    @Test
    void deleteSectionNotLastDownStationFail() {
        // given
        Long 양재시민의숲역_id = StationTestStep.지하철역_생성_후_아이디_추출하기("양재시민의숲역");
        SectionTestRequest sectionRequest = new SectionTestRequest(lineRequest.getDownStationId(), 양재시민의숲역_id, 3);
        SectionTestStep.지하철역_구간_생성하기(sectionRequest, lineId);

        // when
        ExtractableResponse<Response> response = SectionTestStep.지하철역_구간_삭제하기(lineId, lineRequest.getUpStationId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("구간 제거 시 구간이 1개인 경우 역 삭제 불가")
    @Test
    void deleteOnlyOneSectionNotPossible() {
        // when
        ExtractableResponse<Response> response = SectionTestStep.지하철역_구간_삭제하기(lineId, lineRequest.getDownStationId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
