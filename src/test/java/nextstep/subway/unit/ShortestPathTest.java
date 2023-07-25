package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.ShortestPath;
import nextstep.subway.path.exception.PathNotFoundException;
import nextstep.subway.path.exception.StationNotInGivenLinesException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("최단경로 단위 테스트")
class ShortestPathTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10));
        신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10));
        삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2));

        삼호선.registerSection(new Section(남부터미널역, 양재역, 3));
    }

    @DisplayName("ShortestPath를 생성하면 최단경로가 저장된다.")
    @Test
    void constructSuccess() {
        // when
        ShortestPath shortestPath = new ShortestPath(List.of(이호선, 삼호선, 신분당선), 교대역, 양재역);

        // then
        assertThat(shortestPath.getPath()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(shortestPath.getDistance()).isEqualTo(5);
    }

    @DisplayName("경로가 없을 경우 예외가 발생한다.")
    @Test
    void constructFailByPathNotFound() {
        // given: 역 정보와 노선 정보가 주어진다.
        Station 증미역 = new Station(5L, "증미역");
        Station 여의도역 = new Station(6L, "여의도역");

        Line 구호선 = new Line("9호선", "brown", new Section(증미역, 여의도역, 2));

        // when, then
        assertThatThrownBy(() -> new ShortestPath(List.of(이호선, 삼호선, 신분당선, 구호선), 교대역, 여의도역))
                .isInstanceOf(PathNotFoundException.class);
    }

    @DisplayName("노선에 없는 역 조회")
    @Test
    void constructFailByNotInLines() {
        // given
        Station 노선에_없는_역 = new Station(6L, "노선에 없는 역");

        // when, then
        assertThatThrownBy(() -> new ShortestPath(List.of(이호선, 삼호선, 신분당선), 교대역, 노선에_없는_역))
                .isInstanceOf(StationNotInGivenLinesException.class);
    }
}