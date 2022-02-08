package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathFinderTest {

    @DisplayName("지하철역의 최단거리 경로를 찾는다.")
    @Test
    void find() {
        // given
        Station 서울역 = new Station("서울역");
        Station 수원역 = new Station("수원역");
        Station 곰역 = new Station("곰역");
        Station 수유역 = new Station("수유역");
        Station 판교역 = new Station("판교역");
        Station 부산역 = new Station("부산역");

        int 서울역_수원역_거리 = 10;
        int 수원역_판교역_거리 = 5;
        int 판교역_부산역_거리 = 1;
        int 부산역_곰역_거리 = 1;
        int 수원역_수유역_거리 = 4;
        int 수유역_곰역_거리 = 4;

        Line 간선 = new Line("간선", "blue");
        간선.addSections(서울역, 수원역, 서울역_수원역_거리);
        Line 신분당선 = new Line("신분당선", "blue");
        신분당선.addSections(수원역, 판교역, 수원역_판교역_거리);
        신분당선.addSections(판교역, 부산역, 판교역_부산역_거리);
        신분당선.addSections(부산역, 곰역, 부산역_곰역_거리);
        Line 분당선 = new Line("분당선", "blue");
        분당선.addSections(수원역, 수유역, 수원역_수유역_거리);
        분당선.addSections(수유역, 곰역, 수유역_곰역_거리);

        // when
        List<Line> lines = Arrays.asList(간선, 신분당선, 분당선);
        PathFinder pathFinder = new PathFinder(lines);
        List<Station> shortestPath = pathFinder.getShortestPath(서울역, 곰역);
        int shortestDistance = pathFinder.getShortestDistance(서울역, 곰역);

        //then
        assertThat(shortestPath).containsExactly(서울역, 수원역, 판교역, 부산역, 곰역);
        int 최단경로_거리 = allDistance(서울역_수원역_거리, 수원역_판교역_거리, 판교역_부산역_거리, 부산역_곰역_거리);

        assertThat(shortestDistance).isEqualTo(최단경로_거리);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    void 존재하지_않은_출발역이나_도착역을_조회_할_경우() {
        // given
        Station 서울역 = new Station("서울역");
        Station 수원역 = new Station("수원역");
        Station 곰역 = new Station("곰역");
        Station 수유역 = new Station("수유역");
        Station 판교역 = new Station("판교역");
        Station 부산역 = new Station("부산역");

        Station 구로역 = new Station("구로역");

        int 서울역_수원역_거리 = 10;
        int 수원역_판교역_거리 = 5;
        int 판교역_부산역_거리 = 1;
        int 부산역_곰역_거리 = 1;
        int 수원역_수유역_거리 = 4;
        int 수유역_곰역_거리 = 4;

        Line 간선 = new Line("간선", "blue");
        간선.addSections(서울역, 수원역, 서울역_수원역_거리);
        Line 신분당선 = new Line("신분당선", "blue");
        신분당선.addSections(수원역, 판교역, 수원역_판교역_거리);
        신분당선.addSections(판교역, 부산역, 판교역_부산역_거리);
        신분당선.addSections(부산역, 곰역, 부산역_곰역_거리);
        Line 분당선 = new Line("분당선", "blue");
        분당선.addSections(수원역, 수유역, 수원역_수유역_거리);
        분당선.addSections(수유역, 곰역, 수유역_곰역_거리);

        // when
        List<Line> lines = Arrays.asList(간선, 신분당선, 분당선);
        PathFinder pathFinder = new PathFinder(lines);

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> pathFinder.getShortestPath(서울역, 구로역));
        Assertions.assertThrows(IllegalArgumentException.class, () -> pathFinder.getShortestDistance(서울역, 구로역));
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우() {
        // given
        Station 서울역 = new Station("서울역");
        Station 수원역 = new Station("수원역");
        Station 곰역 = new Station("곰역");
        Station 수유역 = new Station("수유역");
        Station 판교역 = new Station("판교역");
        Station 부산역 = new Station("부산역");
        Station 구로역 = new Station("구로역");

        int 서울역_구로역_거리 = 10;
        int 수원역_판교역_거리 = 5;
        int 판교역_부산역_거리 = 1;
        int 부산역_곰역_거리 = 1;
        int 수원역_수유역_거리 = 4;
        int 수유역_곰역_거리 = 4;

        Line 간선 = new Line("간선", "blue");
        간선.addSections(서울역, 구로역, 서울역_구로역_거리);
        Line 신분당선 = new Line("신분당선", "blue");
        신분당선.addSections(수원역, 판교역, 수원역_판교역_거리);
        신분당선.addSections(판교역, 부산역, 판교역_부산역_거리);
        신분당선.addSections(부산역, 곰역, 부산역_곰역_거리);
        Line 분당선 = new Line("분당선", "blue");
        분당선.addSections(수원역, 수유역, 수원역_수유역_거리);
        분당선.addSections(수유역, 곰역, 수유역_곰역_거리);

        // when
        List<Line> lines = Arrays.asList(간선, 신분당선, 분당선);
        PathFinder pathFinder = new PathFinder(lines);

        //then
        Assertions.assertThrows(NullPointerException.class, () -> pathFinder.getShortestPath(서울역, 곰역));
        Assertions.assertThrows(NullPointerException.class, () -> pathFinder.getShortestDistance(서울역, 곰역));
    }

    private int allDistance(int... distances) {
        return Arrays.stream(distances)
                .reduce(Integer::sum)
                .getAsInt();
    }
}
