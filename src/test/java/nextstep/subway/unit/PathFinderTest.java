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

    @DisplayName("최단거리 경로를 찾는다.")
    @Test
    void find() {
        // given
        Station 서울역 = new Station("서울역");
        Station 수원역 = new Station("수원역");
        Station 곰역 = new Station("곰역");
        Station 수유역 = new Station("수유역");
        Station 판교역 = new Station("판교역");
        Station 부산역 = new Station("부산역");

        Line 간선 = new Line("간선", "blue");
        간선.addSections(서울역, 수원역, 10);
        Line 신분당선 = new Line("신분당선", "blue");
        신분당선.addSections(수원역, 판교역, 5);
        신분당선.addSections(판교역, 부산역, 1);
        신분당선.addSections(부산역, 곰역, 1);
        Line 분당선 = new Line("분당선", "blue");
        분당선.addSections(수원역, 수유역, 4);
        분당선.addSections(수유역, 곰역, 4);

        // when
        List<Line> lines = Arrays.asList(간선, 신분당선, 분당선);
        PathFinder pathFinder = new PathFinder(lines);
        List<Station> shortestPath = pathFinder.getShortestPath(서울역, 곰역);

        //then
        assertThat(shortestPath).containsExactly(서울역, 수원역, 판교역, 부산역, 곰역);
    }
}
