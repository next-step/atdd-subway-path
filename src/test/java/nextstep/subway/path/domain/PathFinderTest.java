package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.exception.SameSourceTargetException;
import nextstep.subway.path.exception.SourceTargetNotReachable;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static nextstep.subway.path.domain.PathFinderHelper.*;

@DisplayName("최단 경로 탐색 테스트")
public class PathFinderTest {
    WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    PathFinder pathFinder;
    List<Station> stations;
    List<Line> lines;
    @BeforeEach
    void setUp(){
        Station station1 = 역_만들기("강남역",1L);
        Station station2 = 역_만들기("역삼역", 2L);
        Station station3 = 역_만들기("선릉역", 3L);
        Station station4 = 역_만들기("잠실역", 4L);
        Station station5 = 역_만들기("부평역",5L);
        Station station6 = 역_만들기("인천역", 6L);

        stations = Arrays.asList(
            station1, station2, station3, station4, station5, station6
        );

        Line line1 = new Line("1호선", "red", station1, station2, 20);
        line1.addSection(station2, station3, 20);
        Line line2 = new Line("2호선", "green", station1, station4, 10);
        Line line3 = new Line("3호선", "green", station5, station6, 20);

        lines = Arrays.asList(
                line1, line2, line3
        );

        pathFinder = new PathFinder(stations, lines);
    }

    @DisplayName("가장 짧은 경로 테스트")
    @Test
    void 가장_짧은_경로_반환(){
        //when
        List<Station> pathList = pathFinder.getShortestPathList(1L, 4L);
        List<Long> pathIdList = pathList.stream()
                .map(station -> station.getId())
                .collect(Collectors.toList());

        //then
        assertThat(pathIdList).containsExactlyElementsOf(Arrays.asList(1L, 4L));
    }

    @DisplayName("가장 짧은 경로값 테스트")
    @Test
    void 가장_짧은_경로값_반환(){
        //when
        int length = pathFinder.getShortestPathLength(1L, 4L);

        //then
        assertThat(length).isEqualTo(10);
    }

    @DisplayName("시작, 끝점 같을 때")
    @Test
    void 시작_끝_같을_때(){
        //when
        //then
        assertThatThrownBy(() -> pathFinder.getGraphPath( 1L, 1L))
                .isInstanceOf(SameSourceTargetException.class);
    }

    @DisplayName("서로 만날 수 없는 역일때")
    @Test
    void 서로_만날_수_없는_역일때(){
        //when
        //then
        assertThatThrownBy(() -> pathFinder.getGraphPath( 1L, 6L))
                .isInstanceOf(SourceTargetNotReachable.class);
    }
}
