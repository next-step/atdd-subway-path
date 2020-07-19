package nextstep.subway.path.domain;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.LineStation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 지도 도메인 테스트")
class PathMapTest {

    private List<LineStation> lineStations;

    @BeforeEach
    void setUp() {
        //given
        LineStation lineStation1 = new LineStation(1L, null, 0, 0);
        LineStation lineStation2 = new LineStation(2L, 1L, 5, 5);
        LineStation lineStation3 = new LineStation(3L, 2L, 5, 5);
        LineStation lineStation4 = new LineStation(3L, null, 0, 0);
        LineStation lineStation5 = new LineStation(4L, 3L, 5, 5);
        LineStation lineStation6 = new LineStation(5L, null, 0, 0);
        lineStations = Lists.list(lineStation1, lineStation2, lineStation3, lineStation4, lineStation5, lineStation6);
    }

    @Test
    @DisplayName("최소 소요 시 경로를 조회한다")
    void findDijkstraFastestPath() {
        //given
        PathMap pathMap = PathMap.of(lineStations, PathType.DURATION);

        //when
        List<Long> shortestPath = pathMap.findDijkstraPath(1L, 4L);

        //then
        assertThat(shortestPath).hasSize(4)
                .containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("최단 경로를 조회한다")
    void findDijkstraShortestPath() {
        //given
        PathMap pathMap = PathMap.of(lineStations, PathType.DISTANCE);

        //when
        List<Long> shortestPath = pathMap.findDijkstraPath(1L, 4L);

        //then
        assertThat(shortestPath).hasSize(4)
                .containsExactly(1L, 2L, 3L, 4L);
    }

    @ParameterizedTest
    @EnumSource(value = PathType.class)
    @DisplayName("경로를 조회할 때 경로 지도에 존재하지 않는 지하철역으로 요청이 들어오면 에러를 던진다")
    void findDijkstraShortestPathWithNotFoundException(PathType pathType) {
        //given
        PathMap pathMap = PathMap.of(lineStations, pathType);

        //when
        assertThatThrownBy(() -> pathMap.findDijkstraPath(8L, 4L))
                //then
                .isInstanceOf(NotFoundException.class);
    }

    @ParameterizedTest
    @EnumSource(value = PathType.class)
    @DisplayName("경로를 조회할 때 경로 지도에서 서로 이어지지 않은 지하철역으로 요청이 들어오면 에러를 던진다")
    void findDijkstraShortestPathWithNotConnectedStations(PathType pathType) {
        //given
        PathMap pathMap = PathMap.of(lineStations, pathType);

        //when
        assertThatThrownBy(() -> pathMap.findDijkstraPath(1L, 5L))
                //then
                .isInstanceOf(NoPathExistsException.class);
    }


}