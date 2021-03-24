package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DijkstraStrategyTest {

    private Line 이호선;
    private Station 역삼역;
    private Station 강남역;
    private Station 교대역;
    private Station 서초역;
    private Station 방배역;
    private Station 사당역;

    private int 사당역to방배역 = 7;
    private int 방배역to서초역 = 10;
    private int 서초역to교대역 = 12;
    private int 교대역to강남역 = 2;
    private int 강남역to역삼역 = 5;

    private Line 삼호선;
    private Station 고속버스터미널역;
    private Station 남부터미널역;
    private Station 양재역;

    private int 고속버스터미널역to교대역 = 3;
    private int 교대역to남부터미널역 = 5;
    private int 남부터미널역to양재역 = 6;

    private Line 신분당선;
    private int 양재역to강남역 = 1;


    @BeforeEach
    public void setup() {
        이호선 = new Line("이호선", "green");
        역삼역 = new Station(1L, "역삼역");
        강남역 = new Station(2L, "강남역");
        교대역 = new Station(3L, "교대역");
        서초역 = new Station(4L, "서초역");
        방배역 = new Station(5L, "방배역");
        사당역 = new Station(6L, "사당역");

        이호선.addSection(new Section(이호선, 사당역, 방배역, 사당역to방배역));
        이호선.addSection(new Section(이호선, 방배역, 서초역, 방배역to서초역));
        이호선.addSection(new Section(이호선, 서초역, 교대역, 서초역to교대역));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 교대역to강남역));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 강남역to역삼역));

        삼호선 = new Line("삼호선", "orange");
        고속버스터미널역 = new Station(7L, "고속버스터미널역");
        남부터미널역 = new Station(8L, "남부터미널역");
        양재역 = new Station(9L, "양재역");

        삼호선.addSection(new Section(삼호선, 고속버스터미널역, 교대역, 고속버스터미널역to교대역));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 교대역to남부터미널역));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 남부터미널역to양재역));


        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 양재역, 강남역, 양재역to강남역));

    }

    /** 동일호선
     *  사당역 ---> 방배역 ---> 서초역 ---> 교대역 ---> 강남역 ---> 역삼역
     */
    @DisplayName("동일 호선에 대한 최단경로를 다익스트라 알고리즘으로 구한다")
    @Test
    public void getPath() {
        //Given
        DijkstraStrategy dijkstraStrategy = new DijkstraStrategy();

        //When
        Lines lines = new Lines(Arrays.asList(이호선));
        Stations stations = dijkstraStrategy.getPath(사당역, 역삼역, lines);

        //Then
        assertAll(
                () -> assertThat(stations.getStations()).hasSize(6),
                () -> assertThat(stations.getDistance()).isEqualTo(사당역to방배역 + 방배역to서초역 + 서초역to교대역 + 교대역to강남역 + 강남역to역삼역)
        );
    }

    /**
     *  고속버스터미널역 ---> 교대역 ---> 강남역
     */
    @DisplayName("두 가지 경로 선택 중 최단경로를 다익스트라 알고리즘으로 구한다")
    @Test
    public void getPath_case2() {
        //Given
        DijkstraStrategy dijkstraStrategy = new DijkstraStrategy();

        //When
        Lines lines = new Lines(Arrays.asList(삼호선, 이호선));
        Stations stations = dijkstraStrategy.getPath(고속버스터미널역, 강남역, lines);

        //Then
        assertAll(
                () -> assertThat(stations.getStations()).hasSize(3),
                () -> assertThat(stations.getDistance()).isEqualTo(고속버스터미널역to교대역 + 교대역to강남역)
        );
    }

    /**
     *  고속버스터미널역 ---> 교대역 ---> 강남역 ---> 양재역
     */
    @DisplayName("두 가지 경로 선택 중 최단경로를 다익스트라 알고리즘으로 구한다")
    @Test
    public void getPath_case3() {
        //Given
        DijkstraStrategy dijkstraStrategy = new DijkstraStrategy();

        //When
        Lines lines = new Lines(Arrays.asList(삼호선, 이호선, 신분당선));
        Stations stations = dijkstraStrategy.getPath(고속버스터미널역, 양재역, lines);

        //Then
        assertAll(
                () -> assertThat(stations.getStations()).containsExactlyElementsOf(Arrays.asList(고속버스터미널역, 교대역, 강남역, 양재역)),
                () -> assertThat(stations.getDistance()).isEqualTo(고속버스터미널역to교대역 + 교대역to강남역 + 양재역to강남역)
        );
    }
}
