package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.exception.PathFinderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

public class PathFinderTest {

    @DisplayName("경로 조회 성공")
    @Test
    void getPath_success() {
        // given
        Line 이호선 = new Line(1L, "이호선", "green");
        Line 신분당선 = new Line(2L, "신분당선", "red");
        Line 삼호선 = new Line(3L, "삼호선", "orange");
        이호선.addSection(교대, 강남, 10);
        신분당선.addSection(강남, 양재, 10);
        삼호선.addSection(교대, 남부터미널, 2);
        삼호선.addSection(남부터미널, 양재, 3);

        // when
        PathFinder pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선), 교대, 양재);

        // then
        assertThat(pathFinder.getStations()).contains(교대, 남부터미널, 양재);
        assertThat(pathFinder.getDistance()).isEqualTo(5);
    }

    @DisplayName("경로 조회 실패 : 출발역과 도착역이 같은 경우")
    @Test
    void getPath_fail1() {
        // given
        Line 이호선 = new Line(1L, "이호선", "green");
        Line 신분당선 = new Line(2L, "신분당선", "red");
        Line 삼호선 = new Line(3L, "삼호선", "orange");
        이호선.addSection(교대, 강남, 10);
        신분당선.addSection(강남, 양재, 10);
        삼호선.addSection(교대, 남부터미널, 2);
        삼호선.addSection(남부터미널, 양재, 3);

        // when & then
        assertThatThrownBy(() -> new PathFinder(List.of(이호선, 신분당선, 삼호선), 교대, 교대))
                .isInstanceOf(PathFinderException.class);
    }

    @DisplayName("경로 조회 실패 : 출발역과 도착역이 연결이 되어 있지 않은 경우/ 존재하지 않은 출발역이나 도착역을 조회할 경우")
    @Test
    void getPath_fail3() {
        // given
        Line 이호선 = new Line(1L, "이호선", "green");
        Line 신분당선 = new Line(2L, "신분당선", "red");
        Line 삼호선 = new Line(3L, "삼호선", "orange");
        이호선.addSection(교대, 강남, 10);
        신분당선.addSection(강남, 양재, 10);
        삼호선.addSection(교대, 남부터미널, 2);
        삼호선.addSection(남부터미널, 양재, 3);

        // when & then
        assertThatThrownBy(() -> new PathFinder(List.of(이호선, 신분당선, 삼호선), 교대, 성복))
                .isInstanceOf(PathFinderException.class);
    }
}
