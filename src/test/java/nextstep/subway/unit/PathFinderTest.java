package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.SectionEdge;
import nextstep.subway.domain.exception.PathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathFinderTest {

    private PathFinder pathFinder;
    private String 신사역;
    private String 강남역;
    private String 양재역;
    private String 양재시민의숲역;
    private String 잠원역;
    private String 교대역;
    private String 북한역;
    private String 남한역;


    @BeforeEach
    void setUp() {
        신사역 = "신사역";
        강남역 = "강남역";
        양재역 = "양재역";
        양재시민의숲역 = "양재시민의숲역";
        잠원역 = "잠원역";
        교대역 = "교대역";
        북한역 = "북한역";
        남한역 = "남한역";
        pathFinder = new PathFinder(Set.of(
            new SectionEdge(신사역, 강남역, 10),
            new SectionEdge(강남역, 양재역, 10),
            new SectionEdge(양재역, 양재시민의숲역, 10),
            new SectionEdge(신사역, 잠원역, 10),
            new SectionEdge(잠원역, 교대역, 10),
            new SectionEdge(북한역, 남한역, 10)
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
        Path path = pathFinder.findPath(양재시민의숲역, 교대역);
        // when
        List<String> shortedPath = path.getShortestPath();
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
        assertThatThrownBy(() -> pathFinder.findPath(양재시민의숲역, 양재시민의숲역))
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
        assertThatThrownBy(() -> pathFinder.findPath(양재시민의숲역, "잠실역"))
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
        assertThatThrownBy(() -> pathFinder.findPath(북한역, 잠원역))
            .isInstanceOf(PathException.SourceTargetNotConnectedException.class);
    }
}
