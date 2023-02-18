package nextstep.subway.unit;

import nextstep.subway.applicaion.JGraphPathFinder;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.path.InvalidPathException;
import nextstep.subway.domain.exception.path.SourceAndTargetCannotBeSameException;
import nextstep.subway.domain.exception.path.StationNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.AssertionUtils.목록은_다음을_순서대로_포함한다;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 최단경로 조회 테스트")
public class PathFinderTest {
    private PathFinder pathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 신도림역;
    private Station 구로역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 일호선;
    private List<Line> 노선목록;

    /**
     * 교대역 --- *2호선(10)* --- 강남역
     * |                        |
     * *3호선(2)*             *신분당선(10)*
     * |                        |
     * 남부터미널역 --- *3호선(3)* -양재
     * <p>
     * 구로역 *1호선(10)* 신도림역
     */
    @BeforeEach
    public void setUp() {
        pathFinder = new JGraphPathFinder();

        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");
        신도림역 = new Station("신도림역");
        구로역 = new Station("구로역");

        이호선 = new Line("이호선", "green");
        신분당선 = new Line("신분당선", "red");
        삼호선 = new Line("삼호선", "orange");
        일호선 = new Line("일호선", "blue");

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        일호선.addSection(new Section(일호선, 신도림역, 구로역, 10));

        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        노선목록 = List.of(이호선, 신분당선, 삼호선, 일호선);
    }

    @DisplayName("최단 경로를 조회한다.")
    @Test
    void findPath() {
        final PathResponse path = pathFinder.findPath(노선목록, 교대역, 양재역);

        final List<String> 경로의_역_이름_목록 = path.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(path.getDistance()).isEqualTo(5),
                () -> 목록은_다음을_순서대로_포함한다(경로의_역_이름_목록, 교대역.getName(), 남부터미널역.getName(), 양재역.getName())
        );
    }

    @DisplayName("노선에 등록되지 않은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenStationsNotRegistered() {
        final Station 부산역 = new Station("부산역");

        assertThatThrownBy(() -> pathFinder.findPath(노선목록, 교대역, 부산역))
                .isInstanceOf(StationNotRegisteredException.class);
    }

    @DisplayName("출발역이 이어지지 않은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenSourceStationsNotLinked() {
        assertThatThrownBy(() -> pathFinder.findPath(노선목록, 신도림역, 강남역))
                .isInstanceOf(InvalidPathException.class);
    }

    @DisplayName("도착역이 이어지지 않은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenTargetStationsNotLinked() {
        assertThatThrownBy(() -> pathFinder.findPath(노선목록, 강남역, 신도림역))
                .isInstanceOf(InvalidPathException.class);
    }

    @DisplayName("출발역과 도착역이 같은 역의 경로를 조회하면 오류가 발생한다.")
    @Test
    void cannotFindPathWhenSourceStationAndTargetStationAreSame() {
        assertThatThrownBy(() -> pathFinder.findPath(노선목록, 강남역, 강남역))
                .isInstanceOf(SourceAndTargetCannotBeSameException.class);
    }
}
