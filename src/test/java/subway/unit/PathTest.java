package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.line.Line;
import subway.path.Path;
import subway.section.Section;
import subway.station.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 삼성역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Section 이호선_첫번째_구간;
    private Section 삼호선_첫번째_구간;
    private Section 삼호선_두번째_구간;
    private Section 신분당선_첫번째_구간;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        삼성역 = new Station(5L, "삼성역");

        이호선 = new Line(1L, "2호선", "green");
        삼호선 = new Line(2L, "3호선", "orange");
        신분당선 = new Line(3L, "신분당선", "red");

        이호선_첫번째_구간 = new Section(1L, 교대역, 강남역, 10L, 이호선);
        삼호선_첫번째_구간 = new Section(2L, 교대역, 남부터미널역, 2L, 삼호선);
        삼호선_두번째_구간 = new Section(3L, 남부터미널역, 양재역, 3L, 삼호선);
        신분당선_첫번째_구간 = new Section(4L, 강남역, 양재역, 10L, 신분당선);
    }

    /**
     * Given 출발역과 도착역이 주어질 때,
     * When 지하철 경로를 조회하면
     * Then 출발역과 도착역 사이의 역 목록과 거리를 조회할 수 있다.
     */
    @DisplayName("지하철 경로를 조회하면 출발역과 도착역 사이의 역 목록과 거리를 조회할 수 있다.")
    @Test
    void 두_역_사이의_지하철_경로를_조회하면_출발역과_도착역_사이_역_목록과_거리를_조회할_수_있다() {
        // given

        // when
        Path path = new Path(
                List.of(이호선_첫번째_구간, 삼호선_첫번째_구간, 삼호선_두번째_구간, 신분당선_첫번째_구간),
                교대역,
                양재역
        );
        List<Station> stations = path.getStations();
        long distance = path.getDistance();

        // then
        assertThat(stations).hasSize(3);
        assertThat(stations.get(0).getName()).isEqualTo("교대역");
        assertThat(stations.get(1).getName()).isEqualTo("남부터미널역");
        assertThat(stations.get(2).getName()).isEqualTo("양재역");
        assertThat(distance).isEqualTo(5L);
    }

    /**
     * When 출발역과 도착역이 같다면,
     * Then IllegalArgumentException을 던진다.
     */
    @DisplayName("출발역과 도착역이 같다면 오류를 발생시킨다.")
    @Test
    void 출발역과_도착역이_같다면_오류를_발생시킨다() {
        // given

        // when

        //then
        assertThatThrownBy(() -> new Path(
                List.of(이호선_첫번째_구간, 삼호선_첫번째_구간, 삼호선_두번째_구간, 신분당선_첫번째_구간),
                교대역,
                교대역
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 같습니다.");
    }

    /**
     * When 출발역과 도착역이 연결되어있지 않다면,
     * Then IllegalArgumentException을 던진다.
     */
    @DisplayName("출발역과 도착역이 연결되어있지 않다면 오류를 발생시킨다.")
    @Test
    void 출발역과_도착역이_연결되어있지_않다면_오류를_발생시킨다() {
        // given

        // when

        //then
        assertThatThrownBy(() -> new Path(
                List.of(이호선_첫번째_구간, 삼호선_첫번째_구간, 삼호선_두번째_구간, 신분당선_첫번째_구간),
                삼성역,
                교대역
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역 또는 도착역이 연결되어 있지 않습니다");
    }
}
