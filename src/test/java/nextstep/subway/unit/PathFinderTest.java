package nextstep.subway.unit;

import static nextstep.subway.support.fixture.LineFixture.이호선_색;
import static nextstep.subway.support.fixture.LineFixture.이호선_이름;
import static nextstep.subway.support.fixture.StationFixture.강남역_이름;
import static nextstep.subway.support.fixture.StationFixture.교대역_이름;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Path;
import nextstep.subway.domain.entity.PathFinder;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.support.fixture.LineFixture;
import nextstep.subway.support.fixture.SectionFixture;
import nextstep.subway.support.fixture.StationFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathFinderTest {


    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    Section 교대역_강남역_구간;
    Section 강남역_양재역_구간;
    Section 교대역_남부터미널역_구간;
    Section 남부터미널역_양재역_구간;


    /**
     * 교대역   --- 2호선, 10 ----    강남역 |                            | 3호선, 2                   신분당선, 10 |
     * | 남부터미널역  --- 3호선, 3 ---   양재
     */
    void 이호선_삼호선_신분당선_노선의_구간_존재() {
        교대역 = StationFixture.giveOne(1L, 교대역_이름);
        강남역 = StationFixture.giveOne(2L, 강남역_이름);
        양재역 = StationFixture.giveOne(3L, "양재역");
        남부터미널역 = StationFixture.giveOne(4L, "남부터미널역");

        이호선 = LineFixture.giveOne(1L, 이호선_이름, 이호선_색);
        삼호선 = LineFixture.giveOne(2L, "3호선", "orange");
        신분당선 = LineFixture.giveOne(3L, "신분당선", "red");

        교대역_강남역_구간 = SectionFixture.giveOne(1L, 이호선, 교대역, 강남역, 10L);
        교대역_남부터미널역_구간 = SectionFixture.giveOne(2L, 삼호선, 교대역, 남부터미널역, 2L);
        남부터미널역_양재역_구간 = SectionFixture.giveOne(3L, 삼호선, 남부터미널역, 양재역, 3L);
        강남역_양재역_구간 = SectionFixture.giveOne(4L, 신분당선, 강남역, 양재역, 10L);

        이호선.addSection(교대역_강남역_구간);
        삼호선.addSection(교대역_남부터미널역_구간);
        삼호선.addSection(남부터미널역_양재역_구간);
        신분당선.addSection(강남역_양재역_구간);
    }

    @DisplayName("지하철 최단거리를 조회한다.")
    @Test
    void findShortestPath() {
        // given
        이호선_삼호선_신분당선_노선의_구간_존재();
        PathFinder pathFinder = new PathFinder(List.of(이호선, 삼호선, 신분당선));

        // when
        Path 교대역_양재역_최단경로 = pathFinder.findShortestPath(교대역, 양재역);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(교대역_양재역_최단경로.getStations()).containsExactly(교대역, 남부터미널역, 양재역);
            assertThat(교대역_양재역_최단경로.getDistance()).isEqualTo(5L);
        });

    }

}
