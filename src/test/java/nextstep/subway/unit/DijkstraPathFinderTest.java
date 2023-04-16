package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.exception.PathFinderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DijkstraPathFinderTest {
    private PathFinder pathFinder;

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Station 청명역;
    private Station 영통역;

    private Line 강남_2호선;
    private Line 수인_분당선;

    @BeforeEach
    void setUp() {
        강남_2호선 = new Line("강남_2호선", "green");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 15));

        수인_분당선 = new Line("수인_분당선", "yellow");
        청명역 = new Station("청명역");
        영통역 = new Station("영통역");
        수인_분당선.addSection(new Section(수인_분당선, 청명역, 영통역, 20));

        pathFinder = new DijkstraPathFinder(List.of(강남_2호선, 수인_분당선));
    }

    @Test
    void 경로를_조회한다() {
        // when
        Path path = pathFinder.findPath(강남역, 삼성역);
        
        // then
        List<String> stationNames = path.getStations().stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());
        assertThat(stationNames).containsExactly(강남역.getName(), 역삼역.getName(), 삼성역.getName());
        assertThat(path.getDistance()).isEqualTo(25);
    }

    @Test
    void 출발역과_도착역이_같으면_경로를_조회_할_수_없다() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 강남역))
                .isInstanceOf(PathFinderException.class)
                .hasMessage("출발역과 도착역은 같을 수 없습니다.");
    }

    @Test
    void 출발역과_도착역이_연결_되어_있지_않으면_경로를_조회_할_수_없다() {
        // given
        Station 사당역 = new Station("사당역");

        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 사당역))
                .isInstanceOf(PathFinderException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }
}
