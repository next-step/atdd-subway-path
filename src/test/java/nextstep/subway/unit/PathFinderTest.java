package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.error.exception.BusinessException;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {

    private PathFinder pathFinder;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        이호선 = new Line("2호선", "green");
        노선에_구간_추가(이호선, 교대역, 강남역, 10);
        신분당선 = new Line("신분당선", "red");
        노선에_구간_추가(신분당선, 강남역, 양재역, 10);
        삼호선 = new Line("3호선", "orange");
        노선에_구간_추가(삼호선, 교대역, 남부터미널역, 2);
        노선에_구간_추가(삼호선, 남부터미널역, 양재역, 3);
        pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));
    }

    @DisplayName("역과 역 사이의 최단 거리 찾기")
    @Test
    void findPath() {
        // when
        final GraphPath path = pathFinder.findPath(교대역, 양재역);

        // then
        assertThat(path.getVertexList()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(path.getWeight()).isEqualTo(5);
    }

    @DisplayName("[Error] 출발역과 도착역이 같은 경우, 에러 발생")
    @Test
    void findPathWithSameSourceAndTarget() {
        // when
        // then
        assertThatThrownBy(() -> {
            pathFinder.findPath(교대역, 교대역);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("[Error] 출발역과 도착역이 연결되어있지 않은 경우, 에러 발생")
    @Test
    void findPathWithDisConnectSourceAndTarget() {
        // given
        final Station 언주역 = new Station("언주역");
        final Station 신논현역 = new Station("신논현역");
        final Line 구호선 = new Line("9호선", "gold");
        노선에_구간_추가(구호선, 언주역, 신논현역, 10);
        final PathFinder pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선, 구호선));

        // when
        // then
        assertThatThrownBy(() -> {
            pathFinder.findPath(교대역, 신논현역);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("[Error] 존재하지 않는 출발역이나 도착역을 조회할 경우, 에러 발생")
    @Test
    void findPathWithNonExistsStation() {
        // given
        final Station 언주역 = new Station("언주역");

        // when
        // then
        assertThatThrownBy(() -> {
            pathFinder.findPath(교대역, 언주역);
        }).isInstanceOf(BusinessException.class);
    }

    private void 노선에_구간_추가(Line line, Station upStation, Station downStation, Integer distance) {
        line.addSection(new Section(line, upStation, downStation, distance));
    }
}
