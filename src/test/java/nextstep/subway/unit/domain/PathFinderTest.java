package nextstep.subway.unit.domain;

import nextstep.subway.applicaion.Dikstra;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.NotFoundException;
import nextstep.subway.domain.exception.SubwayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] 경로 조회 테스트")
class PathFinderTest {

    private Station 오금역;
    private Station 경찰병원역;
    private Station 가락시장역;
    private Station 개롱역;
    Line 삼호선;
    Line 오호선;

    @BeforeEach
    void setUp() {
        오금역 = new Station(1L, "오금역");
        경찰병원역 = new Station(2L, "경찰병원역");
        가락시장역 = new Station(3L, "가락시장역");
        개롱역 = new Station(4L, "개롱역");

        삼호선 = new Line("3호선", "oragne");
        오호선 = new Line("5호선", "purple");
    }

    @DisplayName("경로를 확인할 수 있다.")
    @Test
    void shortestPath() {
        // given
        삼호선.addSection(오금역, 경찰병원역, 1);
        삼호선.addSection(경찰병원역, 가락시장역, 2);
        오호선.addSection(오금역, 개롱역, 1);

        // when
        PathFinder pathFinder = new PathFinder(new Dikstra(), List.of(삼호선, 오호선));
        Path path = pathFinder.shortestPath(가락시장역.getId(), 개롱역.getId());

        // then
        assertThat(path).isEqualTo(new Path(List.of(가락시장역, 경찰병원역, 오금역, 개롱역), 4));
    }

    @DisplayName("연결되지 않은 경우 예외가 발생한다.")
    @Test
    void shortestPathException() {
        // given
        삼호선.addSection(경찰병원역, 가락시장역, 2);
        오호선.addSection(오금역, 개롱역, 1);

        // when
        // then
        PathFinder pathFinder = new PathFinder(new Dikstra(), List.of(삼호선, 오호선));
        assertThatThrownBy(() -> pathFinder.shortestPath(가락시장역.getId(), 개롱역.getId()))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("역이 등록되지 않은 경우 예외가 발생한다.")
    @Test
    void notFoundStation() {
        // given
        삼호선.addSection(오금역, 경찰병원역, 2);

        // when
        // then
        PathFinder pathFinder = new PathFinder(new Dikstra(), List.of(삼호선, 오호선));
        assertThatThrownBy(() -> pathFinder.shortestPath(오금역.getId(), Long.MAX_VALUE))
                .isInstanceOf(NotFoundException.class);
    }
}
