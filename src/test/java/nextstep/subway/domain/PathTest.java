package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.exception.CantNotFindPathSameSourceTargetStationException;
import nextstep.subway.domain.exception.NotFoundPathException;
import nextstep.subway.domain.exception.NotFoundSourceAndTargetStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathTest {

    private Station 교대역;
    private Station 양재역;
    private Station 강남역;
    private Station 남부터미널역;
    private List<Station> stations;
    private List<Section> sections;
    private Path path;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");


        Line 신분당선 = new Line("신분당선", "red");
        Line 이호선 = new Line("이호선", "red");
        Line 삼호선 = new Line("신분당선", "red");


        stations = new ArrayList<>();
        stations.addAll(List.of(교대역, 강남역, 양재역, 남부터미널역));
        sections = new ArrayList<>();
        sections.addAll(
            List.of(
                new Section(이호선, 교대역, 강남역, 10),
                new Section(신분당선, 강남역, 양재역, 10),
                new Section(삼호선, 교대역, 남부터미널역, 2),
                new Section(삼호선, 남부터미널역, 양재역, 2))
        );
        path = new Path(stations, sections);
    }


    /**
     * When 출발역과 도착역으로 경로 조회 시
     * Then 경로를 확인 할 수 있다.
     */
    @DisplayName("출발역과 도착역 경로 조회 시 경로를 찾을 수 있다")
    @Test
    void 출발역과_도착역_경로_조회_시_경로를_찾을_수_있다() {
        // When
        PathResponse pathResponse = path.findPath(교대역, 양재역);

        // Then
        assertAll(
            () -> assertThat(pathResponse.getStations()).containsExactly(StationResponse.from(교대역), StationResponse.from(남부터미널역), StationResponse.from(양재역)),
            () -> assertThat(pathResponse.getDistance()).isEqualTo(4)
        );
    }

    /**
     * When && Then 경로 조회 시 출발 역과 도착 역이 같은 경우 조회가 안된다
     */
    @DisplayName("경로 조회 시 출발 역과 도착 역이 같은 경우 조회가 안된다")
    @Test
    void 경로_조회_시_출발_역과_도착_역이_같은_경우_조회가_안된다() {
        // When && Then
        assertThatThrownBy(() -> path.findPath(교대역, 교대역))
            .isInstanceOf(CantNotFindPathSameSourceTargetStationException.class)
            .hasMessage("출발역과 도착역이 동일하면 안됩니다.");
    }

    /**
     * Given 7호선 생성 후
     * When 경로 조회 시 출발역과 도착역이 연결이 되어 있지 않은 경우
     * Then 조회가 안된다
     */
    @DisplayName("경로 조회 시 출발역과 도착역이 연결이 되어 있지 않은 경우 조회가 안된다")
    @Test
    void 경로_조회_시_출발역과_도착역이_연결이_되어_있지_않은_경우_조회가_안된다() {
        // Given
        Station 철산역 = new Station("철산역");
        Station 남구로역 = new Station("남구로역");

        경로에_새로운_구간_추가(철산역, 남구로역, 12);

        // When && Then
        assertThatThrownBy(() -> path.findPath(교대역, 철산역))
            .isInstanceOf(NotFoundPathException.class)
            .hasMessage("경로를 찾을 수 없습니다");
    }

    /**
     * When 존재하지 않은 도착역을 조회 할 경우
     * Then 조회가 안된다
     */
    @DisplayName("경로 조회 시 존재하지 않은 도착역을 조회 할 경우 조회가 안된다")
    @Test
    void 경로_조회_시_존재하지_않은_도착역을_조회_할_경우_조회가_안된다() {
        // Given
        Station 철산역 = new Station("철산역");

        // When
        assertThatThrownBy(() -> path.findPath(교대역, 철산역))
            .isInstanceOf(NotFoundSourceAndTargetStationException.class)
            .hasMessage("경로 조회 시 출발역과 도착역을 찾을 수 없습니다.");
    }

    /**
     * When 존재하지 않은 출발역을 조회 할 경우
     * Then 조회가 안된다
     */
    @DisplayName("경로 조회 시 존재하지 않은 출발역 조회 할 경우 조회가 안된다")
    @Test
    void 경로_조회_시_존재하지_않은_출발역을_조회_할_경우_조회가_안된다() {
        // Given
        Station 철산역 = new Station("철산역");

        // When
        assertThatThrownBy(() -> path.findPath(철산역, 교대역))
            .isInstanceOf(NotFoundSourceAndTargetStationException.class)
            .hasMessage("경로 조회 시 출발역과 도착역을 찾을 수 없습니다.");
    }

    private void 경로에_새로운_구간_추가(Station up, Station down, Integer distance) {
        Section section = new Section(new Line(), up, down, distance);
        stations.add(up);
        stations.add(down);
        sections.add(section);
        path = new Path(stations, sections);
    }

}