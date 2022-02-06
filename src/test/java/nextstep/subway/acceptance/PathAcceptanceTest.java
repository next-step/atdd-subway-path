package nextstep.subway.acceptance;

import nextstep.subway.fixture.LineFixture;
import nextstep.subway.fixture.SectionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineSteps.노선_생성_요청;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.SectionSteps.구간_등록_요청;
import static nextstep.subway.acceptance.StationSteps.역_생성_요청;
import static nextstep.subway.fixture.StationFixture.*;

class PathAcceptanceTest extends AcceptanceTest {

    @DisplayName("최단 경로를 조회한다")
    @Test
    void getShortedPath() {
        // given
        var 강남역_ID = 역_생성_요청(강남역).jsonPath().getLong("id");
        var 신논현역_ID = 역_생성_요청(신논현역).jsonPath().getLong("id");
        var 고속터미널역_ID = 역_생성_요청(고속터미널역).jsonPath().getLong("id");
        var 교대역_ID = 역_생성_요청(교대역).jsonPath().getLong("id");
        var 사평역_ID = 역_생성_요청(사평역).jsonPath().getLong("id");

        노선_생성_요청(LineFixture.of("이호선", 교대역_ID, 강남역_ID, 17));
        노선_생성_요청(LineFixture.of("신분당선", 신논현역_ID, 강남역_ID, 14));
        var 구호선_생성_응답 = 노선_생성_요청(LineFixture.of("구호선", 사평역_ID, 신논현역_ID, 19));
        노선_생성_요청(LineFixture.of("삼호선", 고속터미널역_ID, 교대역_ID, 16));

        구간_등록_요청(구호선_생성_응답.header("Location"), SectionFixture.of(고속터미널역_ID, 사평역_ID, 11));

        // when
        var 최단_경로_요청_응답 = 최단_경로_찾기_요청(교대역_ID, 신논현역_ID);

        // then
        최단_거리_요청_완료(최단_경로_요청_응답, 교대역_ID, 강남역_ID, 신논현역_ID);
    }

    @DisplayName("등록되어 있지 않은 역이 포함된 최단경로를 조회한다")
    @Test
    void getShortestPathNotExistStation() {
        // given
        var 강남역_ID = 역_생성_요청(강남역).jsonPath().getLong("id");
        var 신논현역_ID = 역_생성_요청(신논현역).jsonPath().getLong("id");
        var 고속터미널역_ID = 역_생성_요청(고속터미널역).jsonPath().getLong("id");
        var 교대역_ID = 역_생성_요청(교대역).jsonPath().getLong("id");
        var 사평역_ID = 역_생성_요청(사평역).jsonPath().getLong("id");

        노선_생성_요청(LineFixture.of("이호선", 교대역_ID, 강남역_ID, 17));
        노선_생성_요청(LineFixture.of("신분당선", 신논현역_ID, 강남역_ID, 14));
        var 구호선_생성_응답 = 노선_생성_요청(LineFixture.of("구호선", 사평역_ID, 신논현역_ID, 19));
        노선_생성_요청(LineFixture.of("삼호선", 고속터미널역_ID, 교대역_ID, 16));

        구간_등록_요청(구호선_생성_응답.header("Location"), SectionFixture.of(고속터미널역_ID, 사평역_ID, 11));

        // when
        var 최단_경로_요청_응답 = 최단_경로_찾기_요청(1000L, 10001L);

        // then
        최단_거리_요청_예외(최단_경로_요청_응답);
    }

    @DisplayName("존재하지 않는 경로에 대해 최단경로를 조회한다")
    @Test
    void getShortestNotExistPath() {
        // given
        var 강남역_ID = 역_생성_요청(강남역).jsonPath().getLong("id");
        var 신논현역_ID = 역_생성_요청(신논현역).jsonPath().getLong("id");
        var 고속터미널역_ID = 역_생성_요청(고속터미널역).jsonPath().getLong("id");
        var 교대역_ID = 역_생성_요청(교대역).jsonPath().getLong("id");

        노선_생성_요청(LineFixture.of("신분당선", 신논현역_ID, 강남역_ID, 14));
        노선_생성_요청(LineFixture.of("삼호선", 고속터미널역_ID, 교대역_ID, 16));

        // when
        var 최단_경로_요청_응답 = 최단_경로_찾기_요청(교대역_ID, 신논현역_ID);

        // then
        최단_거리_요청_예외(최단_경로_요청_응답);
    }

}
