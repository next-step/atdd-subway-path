package nextstep.subway.acceptance.line;


import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.line.LineTestUtils.*;
import static nextstep.subway.acceptance.station.StationTestUtils.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    String 강남역_URL;
    String 역삼역_URL;
    String 이호선_URL;
    String 삼성역_URL;
    String 익명역_URL;
    String 판교역_URL;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        강남역_URL = 지하철역_생성(강남역_정보);
        역삼역_URL = 지하철역_생성(역삼역_정보);
        이호선_URL = 지하철_노선_생성(이호선_생성_요청, 강남역_URL, 역삼역_URL, SectionDistance.BIG);

        삼성역_URL = 지하철역_생성(삼성역_정보);
        익명역_URL = 지하철역_생성(익명역_정보);
        판교역_URL = 지하철역_생성(판교역_정보);
    }

    @DisplayName("구간을 등록한다. 기존 구간 A-B에 신규 구간 B-C 추가")
    @Test
    void addSectionABBC() {
        // when
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // then
        지하철_구간이_성공적으로_등록됐다(이호선_URL);
    }

    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 A-B 추가")
    @Test
    void addSectionACAB() {
        // when
        지하철_구간_등록(이호선_URL, 강남역_URL, 익명역_URL, SectionDistance.MEDIUM);

        // then
        지하철_구간이_성공적으로_등록됐다(이호선_URL);
    }

    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 B-C 추가")
    @Test
    void addSectionACBC() {
        // when
        지하철_구간_등록(이호선_URL, 익명역_URL, 역삼역_URL, SectionDistance.MEDIUM);

        // then
        지하철_구간이_성공적으로_등록됐다(이호선_URL);
    }

    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 B-A 추가")
    @Test
    void addSectionACBA() {
        // when
        지하철_구간_등록(이호선_URL, 익명역_URL, 강남역_URL, SectionDistance.BIG);

        // then
        지하철_구간이_성공적으로_등록됐다(이호선_URL);
    }


    @DisplayName("구간 등록 에러, 기존 하행종착역-새 구간 상행역 불일치")
    @Test
    void enrollSectionErrorByInconsistency() {
        // when
        지하철_구간_등록_실패(이호선_URL, 삼성역_URL, 삼성역_URL, SectionDistance.BIG);

        // then
        지하철_구간이_등록되지_않았다(이호선_URL);
    }

    @DisplayName("구간 등록 에러, 새구간 하행역-기존 구간 내 존재")
    @Test
    void enrollSectionErrorByNewDownStationExists() {
        // when
        지하철_구간_등록_실패(이호선_URL, 역삼역_URL, 강남역_URL, SectionDistance.BIG);

        // then
        지하철_구간이_등록되지_않았다(이호선_URL);
    }

    @DisplayName("구간 등록 에러, 역 사이에 새로운 역 등록, 기존 역 사이 길이보다 크거나 같음")
    @Test
    void stationRegistrationBetweenStationsFailBySameOrBiggerDistance() {
        // when
        지하철_구간_등록_실패(이호선_URL, 익명역_URL, 역삼역_URL, SectionDistance.BIG);

        // then
        지하철_구간이_등록되지_않았다(이호선_URL);
    }

    @DisplayName("구간 등록 에러, 상행역과 하행역이 이미 모두 노선에 등록돼있는 구간을 추가")
    @Test
    void stationRegistrationFailByAlreadyExistingTopStationAndDownStation() {
        // given
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // when
        지하철_구간_등록_실패(이호선_URL, 강남역_URL, 역삼역_URL, SectionDistance.MEDIUM);
        지하철_구간_등록_실패(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.MEDIUM);

        // then
        노선의역_개수_변화가_없다(이호선_URL, 3);
    }

    @DisplayName("구간 등록 에러, 상행역과 하행역이 모두 노선에 포함되있지 않은 구간을 추가")
    @Test
    void stationRegistrationFailByLineDoNotContainSectionRelatedStations() {
        // given
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // when
        지하철_구간_등록_실패(이호선_URL, 익명역_URL, 판교역_URL, SectionDistance.MEDIUM);

        // then
        노선의역_개수_변화가_없다(이호선_URL, 3);
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // when
        지하철_구간_삭제(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(삼성역_URL));

        // then
        노선에서_조회되는_역이_줄어든다(이호선_URL, 2);
    }

    @DisplayName("구간을 제거한다. 상행, 하행 종착역 아닌 역 제거")
    @Test
    void removeSectionWhereDeletionStationIsNotTopOrLastStation() {
        // given
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // when
        지하철_구간_삭제(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(역삼역_URL));

        // then
        노선에서_조회되는_역이_줄어든다(이호선_URL, 2);
    }

    @DisplayName("구간을 제거한다. 상행 종착역 제거")
    @Test
    void removeSectionWhereDeletionStationIsTopStation() {
        // given
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // when
        지하철_구간_삭제(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(강남역_URL));

        // then
        노선에서_조회되는_역이_줄어든다(이호선_URL, 2);
    }

    @DisplayName("구간을 제거한다. 하행 종착역 제거")
    @Test
    void removeSectionWhereDeletionStationIsLastStation() {
        // given
        지하철_구간_등록(이호선_URL, 역삼역_URL, 삼성역_URL, SectionDistance.BIG);

        // when
        지하철_구간_삭제(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(삼성역_URL));

        // then
        노선에서_조회되는_역이_줄어든다(이호선_URL, 2);
    }

    @DisplayName("구간을 제거 에러, 상행 종점역과 하행 종점역만 있는 경우")
    @Test
    void removeSectionErrorByOnlyOneSectionLeft() {
        // when
        지하철_구간_삭제_실패(이호선_URL + "/sections?stationId=" + 지하철_아이디_획득(역삼역_URL));

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
