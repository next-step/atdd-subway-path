package nextstep.subway.application.unit;

import nextstep.subway.applicaion.JGraphPathFinder;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nextstep.subway.SubwayFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {
    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        pathFinder = new JGraphPathFinder();
    }

    @DisplayName("경로를 탐색한다.")
    @Test
    void findPath() {
        //given
        final Station 교대역 = 역_생성(1L, "교대역");
        final Station 강남역 = 역_생성(2L, "강남역");
        final Station 양재역 = 역_생성(3L, "양재역");
        final Station 남부터미널역 = 역_생성(4L, "남부터미널역");
        final Line 삼호선 = 노선_생성(1L, "이호선", "green");;
        final Line 신분당선 = 노선_생성(2L, "삼호선", "yellow");;
        final Line 이호선 = 노선_생성(3L, "신분당선", "red");;
        이호선.addSection(구간_생성(교대역, 강남역, 10));
        신분당선.addSection(구간_생성(강남역, 양재역, 10));
        삼호선.addSection(구간_생성(교대역, 남부터미널역, 2));
        삼호선.addSection(구간_생성(남부터미널역, 양재역, 3));

        //when
        PathResponse path = pathFinder.find(List.of(이호선, 삼호선, 신분당선), 교대역, 양재역);

        //then
        assertThat(path.getDistance()).isEqualTo(5);
        assertThat(path.getStationIds()).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
    }

    @DisplayName("연결되지 않은 경로를 탐색하면 실패한다.")
    @Test
    void findPathSetNotConnectedPath() {
        //given
        final Station 교대역 = 역_생성(1L, "교대역");
        final Station 강남역 = 역_생성(2L, "강남역");
        final Station 양재역 = 역_생성(3L, "양재역");
        final Station 남부터미널역 = 역_생성(4L, "남부터미널역");
        final Station 수원역 = 역_생성(5L, "수원역");
        final Station 망포역 = 역_생성(6L, "망포역");
        final Line 삼호선 = 노선_생성(1L, "이호선", "green");
        final Line 신분당선 = 노선_생성(2L, "삼호선", "yellow");
        final Line 이호선 = 노선_생성(3L, "신분당선", "red");
        final Line 분당선 = 노선_생성(4L, "분당선", "black");

        이호선.addSection(구간_생성(교대역, 강남역, 10));
        신분당선.addSection(구간_생성(강남역, 양재역, 10));
        삼호선.addSection(구간_생성(교대역, 남부터미널역, 2));
        삼호선.addSection(구간_생성(남부터미널역, 양재역, 3));
        분당선.addSection(구간_생성(수원역, 망포역, 1));

        //when then
        assertThatThrownBy(() -> pathFinder.find(List.of(이호선, 삼호선, 신분당선), 수원역, 양재역))
                .isInstanceOf(Exception.class);
    }
}
