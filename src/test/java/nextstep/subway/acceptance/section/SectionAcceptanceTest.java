package nextstep.subway.acceptance.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.util.CommonAcceptanceTest;
import nextstep.subway.common.Constant;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.response.AddSectionResponse;
import nextstep.subway.line.presentation.response.CreateLineResponse;
import nextstep.subway.line.presentation.response.ShowLineResponse;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import nextstep.subway.station.presentation.response.CreateStationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineAcceptanceStep.지하철_노선_생성;
import static nextstep.subway.acceptance.line.LineAcceptanceStep.지하철_노선_조회;
import static nextstep.subway.acceptance.section.SectionAcceptanceStep.지하철_구간_삭제;
import static nextstep.subway.acceptance.section.SectionAcceptanceStep.지하철_구간_추가;
import static nextstep.subway.acceptance.station.StationAcceptanceStep.지하철_역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends CommonAcceptanceTest {

    private Long 논현역_ID;
    private Long 신논현역_ID;
    private Long 강남역_ID;
    private Long 양재역_ID;
    private Long 신사역_ID;
    private CreateLineRequest 신분당선_생성_요청;

    @BeforeEach
    protected void setUp() {
        논현역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.논현역)).as(CreateStationResponse.class).getStationId();
        신논현역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.신논현역)).as(CreateStationResponse.class).getStationId();
        강남역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.강남역)).as(CreateStationResponse.class).getStationId();
        양재역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.양재역)).as(CreateStationResponse.class).getStationId();
        신사역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.신사역)).as(CreateStationResponse.class).getStationId();
        신분당선_생성_요청 = CreateLineRequest.of(Constant.신분당선, Constant.빨간색, 논현역_ID, 신논현역_ID, Constant.역_간격_10);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 마지막에 구간을 추가하면
     * Then 노선 마지막에 구간이 추가된다.
     */
    @DisplayName("지하철 노선 마지막에 역을 추가한다.")
    @Test
    void 지하철_노선_마지막에_역을_추가() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();
        AddSectionRequest 신논현_강남_구간_생성_요청 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10);
        AddSectionResponse 신논현_강남_구간_생성_응답 = 지하철_구간_추가(신논현_강남_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // when
        ShowLineResponse 신분당선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);

        // then
        지하철_구간_등록_검증(신논현_강남_구간_생성_응답, 신분당선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 가운데에 구간을 추가하면
     * Then 노선 가운데에 구간이 추가된다.
     */
    @DisplayName("지하철 노선 가운데에 역을 추가한다.")
    @Test
    void 지하철_노선_가운데에_역을_추가() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class); // 논현역, 신논현역
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();
        AddSectionRequest 신논현_강남_구간_생성_요청 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10);  // 논현역, 신논현역, 강남
        AddSectionResponse 신논현_강남_구간_생성_응답 = 지하철_구간_추가(신논현_강남_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // when
        AddSectionRequest 신논현_양재_구간_생성_요청 = AddSectionRequest.of(신논현역_ID, 양재역_ID, Constant.역_간격_5);
        AddSectionResponse 신논현_양재_구간_생성_응답 = 지하철_구간_추가(신논현_양재_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // then
        ShowLineResponse 신분당선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);
        지하철_구간_등록_검증(신논현_양재_구간_생성_응답, 신분당선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 처음에 구간을 추가하면
     * Then 노선 처음에 구간이 추가된다.
     */
    @DisplayName("지하철 노선 처음에 역을 추가한다.")
    @Test
    void 지하철_노선_처음에_역을_추가() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when
        AddSectionRequest 신사_논현_구간_생성_요청 = AddSectionRequest.of(신사역_ID, 논현역_ID, Constant.역_간격_10);
        AddSectionResponse 신사_논현_구간_생성_응답 = 지하철_구간_추가(신사_논현_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // then
        ShowLineResponse 신분당선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);
        지하철_구간_등록_검증(신사_논현_구간_생성_응답, 신분당선_조회_응답);
    }

    /**
     * When 이미 등록된 구간을 등록하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("이미 등록된 구간을 등록하면 구간이 등록되지 않는다.")
    @Test
    void 이미_등록된_구간을_등록() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when
        AddSectionRequest 신논현_논현_구간_생성_요청 = AddSectionRequest.of(논현역_ID, 신논현역_ID, Constant.역_간격_10);

        // then
        ExtractableResponse<Response> 지하철_노선_등록_응답 = 지하철_구간_추가(신논현_논현_구간_생성_요청, 신분당선_ID);
        지하철_구간_등록_예외발생_검증(지하철_노선_등록_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 상행역과 하행역이 모두 노선에 없는 구간을 등록하면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("상행역과 하행역이 모두 노선에 없는 구간을 등록하면 구간이 등록되지 않는다.")
    @Test
    void 상행역과_하행역이_모두_노선에_없는_구간을_등록() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when
        AddSectionRequest 강남_양재_구간_생성_요청 = AddSectionRequest.of(강남역_ID, 양재역_ID, Constant.역_간격_10);

        // then
        ExtractableResponse<Response> 지하철_노선_등록_응답 = 지하철_구간_추가(강남_양재_구간_생성_요청, 신분당선_ID);
        지하철_구간_등록_예외발생_검증(지하철_노선_등록_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 마지막 구간을 제거하면
     * Then 노선 마지막에 구간이 제거된다.
     */
    @DisplayName("지하철 노선 마지막 구간을 제거한다.")
    @Test
    void 지하철_노선_마지막_구간을_삭제() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();
        AddSectionRequest 신논현_강남_구간_생성_요청 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10);
        AddSectionResponse 신논현_강남_노선_등록_응답 = 지하철_구간_추가(신논현_강남_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // when
        지하철_구간_삭제(신분당선_ID, 강남역_ID);
        ShowLineResponse 신분당선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);

        // then
        지하철_구간_삭제_검증(강남역_ID, 신분당선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 가운데 구간을 제거하면
     * Then 노선 가운데 구간이 제거된다.
     */
    @DisplayName("지하철 노선 가운데 구간을 제거한다.")
    @Test
    void 지하철_노선_가운데_구간을_삭제() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();
        AddSectionRequest 신논현_강남_구간_생성_요청 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10);
        AddSectionResponse 신논현_강남_노선_등록_응답 = 지하철_구간_추가(신논현_강남_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // when
        지하철_구간_삭제(신분당선_ID, 신논현역_ID);
        ShowLineResponse 신분당선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);

        // then
        지하철_구간_삭제_검증(신논현역_ID, 신분당선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 노선 처음 구간을 제거하면
     * Then 노선 처음 구간이 제거된다.
     */
    @DisplayName("지하철 노선 처음 구간을 제거한다.")
    @Test
    void 지하철_노선_처음_구간을_삭제() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();
        AddSectionRequest 신논현_강남_구간_생성_요청 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.역_간격_10);
        AddSectionResponse 신논현_강남_노선_등록_응답 = 지하철_구간_추가(신논현_강남_구간_생성_요청, 신분당선_ID).as(AddSectionResponse.class);

        // when
        지하철_구간_삭제(신분당선_ID, 논현역_ID);
        ShowLineResponse 신분당선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);

        // then
        지하철_구간_삭제_검증(논현역_ID, 신분당선_조회_응답);
    }

    /**
     * When 지하철 노선의 구간이 1개인데 역을 삭제하면
     * Then 역이 삭제되지 않는다.
     */
    @DisplayName("지하철 노선의 구간이 1개인 경우 역이 삭제되지 않는다.")
    @Test
    void 남은_구간이_한개인_노선의_구간_삭제() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when & then
        ExtractableResponse<Response> 지하철_노선_삭제_응답 = 지하철_구간_삭제(신분당선_ID, 신논현역_ID);
        지하철_구간_삭제_예외발생_검증(지하철_노선_삭제_응답, HttpStatus.BAD_REQUEST);
    }

    /**
     * When 노선에 상행역 또는 하행역이 없는 구간을 삭제하면
     * Then 구간이 삭제되지 않는다.
     */
    @DisplayName("삭제하려는 구간의 상행역 또는 하행역이 노선에 존재하지 않으면 삭제되지 않는다.")
    @Test
    void 하행역_또는_상행역이_노선에_없는_구간_삭제() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when & then
        ExtractableResponse<Response> 지하철_노선_삭제_응답 = 지하철_구간_삭제(신분당선_ID, 양재역_ID);
        지하철_구간_삭제_예외발생_검증(지하철_노선_삭제_응답, HttpStatus.NOT_FOUND);
    }

    void 지하철_구간_등록_검증(AddSectionResponse addSectionResponse, ShowLineResponse showLineResponse) {
        assertTrue(showLineResponse.getSections().stream()
                .anyMatch(sectionDto ->
                        sectionDto.getUpStation().getName().equals(addSectionResponse.getUpStation().getName())
                                && sectionDto.getDownStation().getName().equals(addSectionResponse.getDownStation().getName())
                ));
    }

    void 지하철_구간_등록_예외발생_검증(ExtractableResponse<Response> extractableResponse, HttpStatus status) {
        assertThat(extractableResponse.statusCode()).isEqualTo(status.value());
    }

    void 지하철_구간_삭제_검증(Long stationId, ShowLineResponse showLineResponse) {
        assertTrue(showLineResponse.getSections().stream()
                .noneMatch(sectionDto ->
                        sectionDto.getDownStation().getStationId().equals(stationId)
                                || sectionDto.getUpStation().getStationId().equals(stationId)
                ));
    }

    void 지하철_구간_삭제_예외발생_검증(ExtractableResponse<Response> extractableResponse, HttpStatus status) {
        assertThat(extractableResponse.statusCode()).isEqualTo(status.value());
    }

}
