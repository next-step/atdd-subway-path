package nextstep.subway.unit;


import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    @DisplayName("최단 경로 조회")
    @Test
    void getShortsPath() {
        // given
        Station 교대역 = new Station("교대역");
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 남부터미널역 = new Station("남부터미널역");

        Line 이호선 = new Line("2호선", "green");
        Line 삼호선 = new Line("3호선", "orange");
        Line 신분당선 = new Line("신분당선", "red");

        Section 교대역_강남역_구간 = new Section(이호선, 교대역, 강남역, 10);
        Section 교대역_남부터미널_구간 = new Section(삼호선, 교대역, 남부터미널역, 2);
        Section 남부터미널_양재역_구간 = new Section(삼호선, 남부터미널역, 양재역, 3);
        Section 강남역_양재역_구간 = new Section(신분당선, 강남역, 양재역, 10);

        이호선.addSection(교대역_강남역_구간);
        삼호선.addSection(교대역_남부터미널_구간);
        삼호선.addSection(남부터미널_양재역_구간);
        신분당선.addSection(강남역_양재역_구간);
        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선);
        PathFinder pathFinder = new PathFinder(lines);

        // when
        List<Station> stations = pathFinder.shortsPathStations(교대역, 양재역);
        int distance = pathFinder.shortsPathDistance(교대역, 양재역);

        // then
        assertThat(stations).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(distance).isEqualTo(5);
    }
}
