package nextstep.subway.section;


import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineTestUtils.*;
import static nextstep.subway.section.SectionUtils.*;
import static nextstep.subway.station.StationTestUtils.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {


    String 상행종착역_URL;
    String 하행종착역_URL;
    String 이호선_URL;
    String 새로운_하행역_URL;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        상행종착역_URL = 지하철역_생성(강남역_정보);
        하행종착역_URL = 지하철역_생성(역삼역_정보);
        이호선_URL = 지하철_노선_생성(이호선_생성_요청, 상행종착역_URL, 하행종착역_URL);

        새로운_하행역_URL = 지하철역_생성(삼성역_정보);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void enrollSection() {
        // when
        지하철_구간_등록(이호선_URL, 구간_등록_요청, 하행종착역_URL, 새로운_하행역_URL);

        // then
        지하철_구간이_성공적으로_등록됐다(이호선_URL);
    }

    @DisplayName("구간 등록 에러, 기존 하행종착역-새 구간 상행역 불일치")
    @Test
    void enrollSectionErrorByInconsistency() {
        // when
        지하철_구간_등록_실패(이호선_URL, 구간_등록_요청, 새로운_하행역_URL, 새로운_하행역_URL);

        // then
        지하철_구간이_등록되지_않았다(이호선_URL);
    }

    @DisplayName("구간 등록 에러, 새구간 하행역-기존 구간 내 존재")
    @Test
    void enrollSectionErrorByNewDownStationExists() {
        // when
        지하철_구간_등록_실패(이호선_URL, 구간_등록_요청, 하행종착역_URL, 상행종착역_URL);
        // then
        지하철_구간이_등록되지_않았다(이호선_URL);
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        지하철_구간_등록(이호선_URL, 구간_등록_요청, 하행종착역_URL, 새로운_하행역_URL);

        // when
        지하철_구간_삭제(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(새로운_하행역_URL));

        // then
        노선에서_조회되는_역이_줄어든다(이호선_URL, 2);
    }

    @DisplayName("구간을 제거 에러, 마지막 구간 아닌 구간 제거")
    @Test
    void removeSectionErrorByRemovingNonLastSection() {
        // given
        지하철_구간_등록(이호선_URL, 구간_등록_요청, 하행종착역_URL, 새로운_하행역_URL);

        // when
        지하철_구간_삭제_실패(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(상행종착역_URL));

        // then
        노선의역_개수_변화가_없다(이호선_URL, 3);
    }

    @DisplayName("구간을 제거 에러, 상행 종점역과 하행 종점역만 있는 경우")
    @Test
    void removeSectionErrorByOnlyOneSectionLeft() {
        // when
        지하철_구간_삭제_실패(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(하행종착역_URL));

        // then
        노선의역_개수_변화가_없다(이호선_URL, 2);
    }

    private void 지하철_구간이_성공적으로_등록됐다(String 노선_url) {
        assertThat(지하철_노선_조회(노선_url).jsonPath().getList("stations", StationResponse.class))
                .hasSize(3);
    }

    private static void 지하철_구간이_등록되지_않았다(String 노선_url) {
        assertThat(지하철_노선_조회(노선_url).response().jsonPath().getList("stations", StationResponse.class))
                .hasSize(2);
    }

    private void 노선의역_개수_변화가_없다(String 노선_url, int 역_개수) {
        assertThat(지하철_노선_조회(노선_url).jsonPath().getList("stations", StationResponse.class))
                .hasSize(역_개수);
    }

    private void 노선에서_조회되는_역이_줄어든다(String 노선_url, int 역_개수) {
        assertThat(지하철_노선_조회(노선_url).jsonPath().getList("stations", StationResponse.class))
                .hasSize(역_개수);
    }
}
