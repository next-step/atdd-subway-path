package nextstep.subway.acceptance.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.util.CommonAcceptanceTest;
import nextstep.subway.common.Constant;
import nextstep.subway.line.presentation.request.AddSectionRequest;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.line.LineApiExtractableResponse.지하철_노선_생성;
import static nextstep.subway.acceptance.line.LineApiExtractableResponse.지하철_노선_조회;
import static nextstep.subway.acceptance.section.SectionApiExtractableResponse.addSection;
import static nextstep.subway.acceptance.section.SectionApiExtractableResponse.deleteSection;
import static nextstep.subway.acceptance.station.StationApiExtractableResponse.지하철_역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends CommonAcceptanceTest {

    CreateLineRequest 신분당선_생성_정보;
    Long 논현역_ID;
    Long 신논현역_ID;
    Long 강남역_ID;
    Long 양재역_ID;

    @BeforeEach
    protected void setUp() {
        논현역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.논현역)).jsonPath().getLong("stationId");
        신논현역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.신논현역)).jsonPath().getLong("stationId");
        강남역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.강남역)).jsonPath().getLong("stationId");
        양재역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.양재역)).jsonPath().getLong("stationId");
        신분당선_생성_정보 = CreateLineRequest.of(Constant.신분당선, Constant.빨간색, 논현역_ID, 신논현역_ID, Constant.기본_역_간격);
    }

    /**
     * When 새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아니면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아니면 지하철 구간이 등록되지 않는다.")
    @Test
    void 노선의_하행_종점역이_아닌_상행역의_지하철_구간을_등록() {
        // given
        Long 신분당선_ID = 지하철_노선_생성(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(양재역_ID, 강남역_ID, Constant.기본_역_간격);

        // then
        assertThat(addSection(구간_생성_정보, 신분당선_ID).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 이미 해당 노선에 등록되어 있는 역이 새로운 구간의 하행역이면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("등록할 역이 이미 있는 지하철 노선에는 구간이 등록되지 않는다.")
    @Test
    void 등록할_역이_이미_있는_지하철_노선에_구간을_등록() {
        // given
        Long 신분당선_ID = 지하철_노선_생성(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 논현역_ID, Constant.기본_역_간격);

        // then
        assertThat(addSection(구간_생성_정보, 신분당선_ID).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 지하철 노선을 조회하면
     * Then 등록한 구간의 역을 조회할 수 있다.
     */
    @DisplayName("지하철 구간을 등록한다.")
    @Test
    void 지하철_구간을_등록() {
        // given
        Long 신분당선_ID = 지하철_노선_생성(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.기본_역_간격);
        assertThat(addSection(구간_생성_정보, 신분당선_ID).statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> 신분당선_조회_결과 = 지하철_노선_조회(신분당선_ID);
        List<Long> 구간_상행역_목록 = 신분당선_조회_결과.jsonPath().getList("sections.upStation.stationId", Long.class);
        assertThat(구간_상행역_목록).containsOnly(논현역_ID, 신논현역_ID);
        List<Long> 구간_하행역_목록 = 신분당선_조회_결과.jsonPath().getList("sections.downStation.stationId", Long.class);
        assertThat(구간_하행역_목록).containsOnly(신논현역_ID, 강남역_ID);
    }

//    private List<Long> 신분당선_역_목록(Long Id {
//
//    }

    /**
     * When 지하철 노선에 등록된 역(하행 종점역)이 아닌 역을 삭제하면
     * Then 구간이 삭제되지 않는다.
     */
    @DisplayName("지하철 노선에 등록된 역(하행 종점역)이 아닌 역을 삭제하면 구간이 삭제되지 않는다.")
    @Test
    void 등록되지_않은_하행역을_삭제() {
        // given
        Long 신분당선_ID = 지하철_노선_생성(신분당선_생성_정보).jsonPath().getLong("lineId");

        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.기본_역_간격);
        addSection(구간_생성_정보, 신분당선_ID);

        // when & then
        assertThat(deleteSection(신분당선_ID, 논현역_ID).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선의 구간이 1개인데 역을 삭제하면
     * Then 역이 삭제되지 않는다.
     */
    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역이 삭제되지 않는다.")
    @Test
    void 남은_구간이_한개인_노선의_역_삭제() {
        // given
        Long 신분당선_ID = 지하철_노선_생성(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when & then
        assertThat(deleteSection(신분당선_ID, 신논현역_ID).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 구간을 생성하고
     * When 생성한 지하철 구간을 삭제하면
     * Then 해당 지하철 구간 정보는 삭제된다.
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void 지하철_구간을_삭제() {
        // given
        Long 신분당선_ID = 지하철_노선_생성(신분당선_생성_정보).jsonPath().getLong("lineId");

        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 강남역_ID, Constant.기본_역_간격);
        addSection(구간_생성_정보, 신분당선_ID);

        // when
        deleteSection(신분당선_ID, 강남역_ID);

        // then
        ExtractableResponse<Response> 신분당선_조회_결과 = 지하철_노선_조회(신분당선_ID);
        List<Long> 구간_상행역_목록 = 신분당선_조회_결과.jsonPath().getList("sections.upStation.stationId", Long.class);
        assertThat(구간_상행역_목록).doesNotContain(강남역_ID);
        List<Long> 구간_하행역_목록 = 신분당선_조회_결과.jsonPath().getList("sections.downStation.stationId", Long.class);
        assertThat(구간_하행역_목록).doesNotContain(강남역_ID);
    }

//    /**
//     * Given 지하철 노선을 생성하고
//     * When 노선 가운데에 구간을 추가하면
//     * Then 노선 가운데에 구간이 추가된다.
//     */
//    @DisplayName("지하철 노선 가운데에 역을 추가한다.")
//    @Test
//    void 지하철_노선_가운데에_역을_추가() {
//        // given
//        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 강남역_ID, 기본_역_간격);
//        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");
//
//        // when
//        AddSectionRequest 논현역_신논현역_구간_생성_정보 = AddSectionRequest.of(논현역_ID, 신논현역_ID, 기본_역_간격);
//        addSection(논현역_신논현역_구간_생성_정보, 신분당선_ID);
//
//        // then
//        ExtractableResponse<Response> 신분당선_조회_결과 =  selectLine(신분당선_ID);
//        List<Long> 구간_상행역_목록 = 신분당선_조회_결과.jsonPath().getList("sections.upStation.stationId", Long.class);
//        assertThat(구간_상행역_목록).doesNotContain(논현역_ID, 신논현역_ID);
//        List<Long> 구간_하행역_목록 = 신분당선_조회_결과.jsonPath().getList("sections.downStation.stationId", Long.class);
//        assertThat(구간_하행역_목록).doesNotContain(신논현역_ID, 강남역_ID);
//    }


}
