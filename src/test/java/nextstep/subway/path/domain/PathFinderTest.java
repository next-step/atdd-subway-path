package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.DijkstraStrategy;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PathFinderTest {


    private Line 이호선;
    private Station 역삼역;
    private Station 강남역;
    private Station 교대역;
    private Station 서초역;
    private Station 방배역;
    private Station 사당역;


    private Station 대방역;


    @BeforeEach
    public void setup() {
        이호선 = new Line("이호선", "green");
        역삼역 = new Station(1L,"역삼역");
        강남역 = new Station(2L,"강남역");
        교대역 = new Station(3L,"교대역");
        서초역 = new Station(4L,"서초역");
        방배역 = new Station(5L,"방배역");
        사당역 = new Station(6L,"사당역");

        이호선.addSection(new Section(이호선, 사당역, 방배역, 7));
        이호선.addSection(new Section(이호선, 방배역, 서초역, 10));
        이호선.addSection(new Section(이호선, 서초역, 교대역, 12));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 3));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 5));


        대방역 = new Station(7L,"대방역");
    }

    @DisplayName("가장 빠른 경로를 조회한다.")
    @Test
    public void createInstance(){
        //Given

        PathFinder pathFinder = new PathFinder(new DijkstraStrategy(Arrays.asList(이호선)));

        //When
        Stations stations = pathFinder.findShortestPath(교대역, 역삼역);

        //Then
        assertThat(stations.getStations()).hasSize(3);
    }

    @DisplayName("출발역과 도착역이 같을 경우 예외처리 한다.")
    @Test
    public void startAndEndStationTheEquals(){
        assertThatThrownBy(() -> {
            PathFinder pathFinder = new PathFinder(new DijkstraStrategy(Arrays.asList(이호선)));
            pathFinder.findShortestPath(교대역, 교대역);
        }).isInstanceOf(CannotFindPathException.class);
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않을 경우")
    @Test
    public void notConnectStation(){
        assertThatThrownBy(() -> {
            PathFinder pathFinder = new PathFinder(new DijkstraStrategy(Arrays.asList(이호선)));
            pathFinder.findShortestPath(교대역, 대방역);
        }).isInstanceOf(CannotFindPathException.class);
    }
}
