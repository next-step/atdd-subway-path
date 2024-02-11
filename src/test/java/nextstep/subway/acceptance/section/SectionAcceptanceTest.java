package nextstep.subway.acceptance.section;

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

import static nextstep.subway.acceptance.line.LineApiExtractableResponse.createLine;
import static nextstep.subway.acceptance.line.LineApiExtractableResponse.selectLine;
import static nextstep.subway.acceptance.section.SectionApiExtractableResponse.addSection;
import static nextstep.subway.acceptance.section.SectionApiExtractableResponse.deleteSection;
import static nextstep.subway.acceptance.station.StationApiExtractableResponse.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends CommonAcceptanceTest {

    String 신분당선 = Constant.신분당선;
    String 논현역 = Constant.논현역;
    String 신논현역 = Constant.신논현역;
    String 강남역 = Constant.강남역;
    String 양재역 = Constant.양재역;
    String 빨간색 = Constant.빨간색;
    int 기본_역_간격 = Constant.기본_역_간격;
    Long 논현역_ID;
    Long 신논현역_ID;
    Long 강남역_ID;
    Long 양재역_ID;

    @BeforeEach
    protected void setUp() {
        논현역_ID = createStation(CreateStationRequest.from(논현역)).jsonPath().getLong("stationId");
        신논현역_ID = createStation(CreateStationRequest.from(신논현역)).jsonPath().getLong("stationId");
        강남역_ID = createStation(CreateStationRequest.from(강남역)).jsonPath().getLong("stationId");
        양재역_ID = createStation(CreateStationRequest.from(양재역)).jsonPath().getLong("stationId");
    }

    /**
     * When 새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아니면
     * Then 구간이 등록되지 않는다.
     */
    @DisplayName("새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아니면 지하철 구간이 등록되지 않는다.")
    @Test
    void 노선의_하행_종점역이_아닌_상행역의_지하철_구간을_등록() {
        // given
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(양재역_ID, 강남역_ID, 기본_역_간격);

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
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 논현역_ID, 기본_역_간격);

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
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        // when
        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 강남역_ID, 기본_역_간격);
        assertThat(addSection(구간_생성_정보, 신분당선_ID).statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<Long> responseLineName = selectLine(신분당선_ID).jsonPath().getList("stations.stationId", Long.class);
        assertThat(responseLineName).contains(논현역_ID, 신논현역_ID, 강남역_ID);
    }

    /**
     * When 지하철 노선에 등록된 역(하행 종점역)이 아닌 역을 삭제하면
     * Then 구간이 삭제되지 않는다.
     */
    @DisplayName("지하철 노선에 등록된 역(하행 종점역)이 아닌 역을 삭제하면 구간이 삭제되지 않는다.")
    @Test
    void 등록되지_않은_하행역을_삭제() {
        // given
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 강남역_ID, 기본_역_간격);
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
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

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
        CreateLineRequest 신분당선_생성_정보 = CreateLineRequest.of(신분당선, 빨간색, 논현역_ID, 신논현역_ID, 기본_역_간격);
        Long 신분당선_ID = createLine(신분당선_생성_정보).jsonPath().getLong("lineId");

        AddSectionRequest 구간_생성_정보 = AddSectionRequest.of(신논현역_ID, 강남역_ID, 기본_역_간격);
        addSection(구간_생성_정보, 신분당선_ID);

        // when
        deleteSection(신분당선_ID, 강남역_ID);

        // then
        List<String> responseLineName = selectLine(신분당선_ID).jsonPath().get("stations.name");
        assertThat(responseLineName).doesNotContain(강남역);
    }

}
