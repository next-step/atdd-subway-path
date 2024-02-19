package nextstep.subway.unit;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.SectionEdge;
import nextstep.subway.domain.exception.PathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private PathFinder pathFinder;
    private Long 신사역;
    private Long 강남역;
    private Long 양재역;
    private Long 양재시민의숲역;
    private Long 잠원역;
    private Long 교대역;
    private Long 북한역;
    private Long 남한역;


    @BeforeEach
    void setUp() {
        신사역 = 1L;
        강남역 = 2L;
        양재역 = 3L;
        양재시민의숲역 = 4L;
        잠원역 = 5L;
        교대역 = 6L;
        북한역 = 7L;
        남한역 = 8L;
        pathFinder = new PathFinder(Set.of(
                new SectionEdge(신사역, 강남역, 10),
                new SectionEdge(강남역, 양재역, 10),
                new SectionEdge(양재역, 양재시민의숲역, 10),
                new SectionEdge(신사역, 잠원역, 10),
                new SectionEdge(잠원역, 교대역, 10),
                new SectionEdge(북한역, 남한역, 10),
                new SectionEdge(잠원역, 교대역, 10)
        ));
    }

    /**
     * When findPath 메서드를 호출하면
     * Then 최단 경로를 리턴한다.
     */
    @Test
    void findPath() {
        // given
        Path path = pathFinder.findShortedPath(양재시민의숲역, 교대역);
        // when
        List<Long> shortedPath = path.getVertexList();
        // then
        assertThat(shortedPath).containsExactly(양재시민의숲역, 양재역, 강남역, 신사역, 잠원역, 교대역);
        assertThat(path.getDistance()).isEqualTo(50);
    }

    /**
     * When 같은 출발역, 도착역으로 findPath 메서드를 호출하면
     * Then 에러가 발생한다
     */
    @Test
    void findPathWithSameStation() {
        // given
        // when
        // then
        assertThatThrownBy(() -> pathFinder.findShortedPath(양재시민의숲역, 양재시민의숲역))
                .isInstanceOf(PathException.PathSourceTargetSameException.class);
    }

    /**
     * When 존재하지 않는 출발역, 도착역으로 findPath 메서드를 호출하면
     * Then 에러가 발생한다
     */
    @Test
    void findPathWithNotExistStation() {
        // given
        // when
        // then
        assertThatThrownBy(() -> pathFinder.findShortedPath(양재시민의숲역, 9L))
                .isInstanceOf(PathException.PathNotFoundException.class);
    }

    /**
     * When 서로 연결되어 있지 않은 출발역, 도착역으로 findPath 메서드를 호출하면
     * Then 에러가 발생한다
     */
    @Test
    void findPathWithNotConnectedStation() {
        // given
        // when
        // then
        assertThatThrownBy(() -> pathFinder.findShortedPath(북한역, 잠원역))
                .isInstanceOf(PathException.SourceTargetNotConnectedException.class);
    }
}
