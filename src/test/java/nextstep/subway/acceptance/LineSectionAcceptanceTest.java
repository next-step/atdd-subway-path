package nextstep.subway.acceptance;

import nextstep.subway.fixture.SectionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.신분당선_생성_완료;
import static nextstep.subway.acceptance.SectionSteps.*;
import static nextstep.subway.acceptance.StationSteps.역_생성_요청;
import static nextstep.subway.fixture.StationFixture.*;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // given
        역_생성_요청(역삼역);

        var 노선_생성_응답 = 신분당선_생성_완료();

        // when
        var lineUri = 노선_생성_응답.header("Location");
        var 구간1 = SectionFixture.of(2L, 3L, 10);
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
        역_생성_요청(역삼역);

        var 노선_생성_응답 = 신분당선_생성_완료();
        var 구간1 = SectionFixture.of(2L, 3L, 10);

        var lineUri = 노선_생성_응답.header("Location");
        구간_등록_요청(lineUri, 구간1);

        // when
        var 구간_삭제_응답 = 구간_삭제_요청(lineUri, 3L);

        // then
        구간_삭제_성공(구간_삭제_응답);
    }

}
