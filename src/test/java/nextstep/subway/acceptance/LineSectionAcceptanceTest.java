package nextstep.subway.acceptance;

import nextstep.subway.fixture.SectionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.SectionSteps.*;
import static nextstep.subway.acceptance.StationSteps.역_생성_요청;
import static nextstep.subway.fixture.LineFixture.신분당선;
import static nextstep.subway.fixture.StationFixture.*;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("역 사이에 새로운 역을 등록")
    @Test
    void addSectionBetweenSection() {
        // given
        역_생성_요청(신논현역);
        역_생성_요청(양재역);
        var 노선_생성_응답 = 노선_생성_요청(신분당선);

        var 역_생성_응답 = 역_생성_요청(강남역);

        // when
        var lineUri = 노선_생성_응답.header("Location");
        var 하행_종점역_ID = 노선_생성_응답.jsonPath().getList("stations.id", Long.class).get(1);
        long 새로운역_ID = 역_생성_응답.jsonPath().getLong("id");
        var 구간_등록_응답 = 구간_등록_요청(lineUri, SectionFixture.of(새로운역_ID, 하행_종점역_ID, 5));

        // then
        구간_등록_성공(구간_등록_응답);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addSectionToTop() {
        // given
        역_생성_요청(강남역);
        역_생성_요청(양재역);
        var 노선_생성_응답 = 노선_생성_요청(신분당선);

        var 역_생성_응답 = 역_생성_요청(신논현역);

        // when
        var lineUri = 노선_생성_응답.header("Location");
        var 상행_종점역_ID = 노선_생성_응답.jsonPath().getList("stations.id", Long.class).get(0);
        var 새로운역_ID = 역_생성_응답.jsonPath().getLong("id");
        var 구간_등록_응답 = 구간_등록_요청(lineUri, SectionFixture.of(새로운역_ID, 상행_종점역_ID, 5));

        // then
        구간_등록_성공(구간_등록_응답);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addLineSection() {
        // given
        var 노선_생성_응답 = 신분당선_생성_완료();
        var 역_생성_응답 = 역_생성_요청(역삼역);

        // when
        var lineUri = 노선_생성_응답.header("Location");
        var 하행_종점역_ID = 노선_생성_응답.jsonPath().getList("stations.id", Long.class).get(1);
        var 새로운역_ID = 역_생성_응답.jsonPath().getLong("id");

        var 구간1 = SectionFixture.of(하행_종점역_ID, 새로운역_ID, 10);
        var 구간_등록_응답 = 구간_등록_요청(lineUri, 구간1);

        // then
        구간_등록_성공(구간_등록_응답);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        var 노선_생성_응답 = 신분당선_생성_완료();

        var 역_생성_응답 = 역_생성_요청(역삼역);
        var 하행_종점역_ID = 노선_생성_응답.jsonPath().getList("stations.id", Long.class).get(1);
        var 새로운역_ID = 역_생성_응답.jsonPath().getLong("id");

        var 구간1 = SectionFixture.of(하행_종점역_ID, 새로운역_ID, 10);

        var lineUri = 노선_생성_응답.header("Location");
        구간_등록_요청(lineUri, 구간1);

        // when
        var 구간_삭제_응답 = 구간_삭제_요청(lineUri, 새로운역_ID);

        // then
        구간_삭제_성공(구간_삭제_응답);
    }


    @DisplayName("역 사이에 새로운 역을 생성")
    @Test
    void addSectionBetween() {
        // given
        var 노선_생성_응답 = 이호선_생성_완료();

        var 역_생성_응답 = 역_생성_요청(강남역);

        // when
        var lineUri = 노선_생성_응답.header("Location");
        var 하행_종점역_ID = 노선_생성_응답.jsonPath().getList("stations.id", Long.class).get(1);
        var 새로운역_ID = 역_생성_응답.jsonPath().getLong("id");

        var 구간1 = SectionFixture.of(새로운역_ID, 하행_종점역_ID, 4);
        var 구간_등록_응답 = 구간_등록_요청(lineUri, 구간1);

        // then
        구간_등록_성공(구간_등록_응답);
    }

    @DisplayName("역 사이에 기존 구간보다 더 긴 구간을 추가")
    @Test
    void addSectionBetweenLong() {
        // given
        var 노선_생성_응답 = 이호선_생성_완료();

        var 역_생성_응답 = 역_생성_요청(강남역);

        // when
        var lineUri = 노선_생성_응답.header("Location");
        var 하행_종점역_ID = 노선_생성_응답.jsonPath().getList("stations.id", Long.class).get(1);
        var 새로운역_ID = 역_생성_응답.jsonPath().getLong("id");

        var 구간1 = SectionFixture.of(새로운역_ID, 하행_종점역_ID, 11);
        var 구간_등록_응답 = 구간_등록_요청(lineUri, 구간1);

        // then
        구간_생성_예외(구간_등록_응답);
    }
}
