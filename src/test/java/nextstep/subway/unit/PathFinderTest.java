package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.applicaion.facade.PathFinder;
import nextstep.subway.applicaion.strategy.strategy.DijkstraPathFindStrategy;
import nextstep.subway.enums.SubwayErrorMessage;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private Line 분당선;
    private Line 신분당선;
    private Line 경의중앙선;
    private Section 강남_선릉_거리2_구간;
    private Section 선릉_왕십리_거리10_구간;
    private Section 선릉_왕십리_거리5_구간;
    private Section 역삼_영통_거리5_구간;

    private List<Section> 전체구간;
    private List<Station> 지하철_목록;


    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
        신분당선 = FakeLineFactory.신분당선();
        경의중앙선 = FakeLineFactory.경의중앙선();

        강남_선릉_거리2_구간 = new Section(분당선, FakeStationFactory.강남역(), FakeStationFactory.선릉역(), 2);
        선릉_왕십리_거리10_구간 = new Section(분당선, FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 10);
        선릉_왕십리_거리5_구간 = new Section(신분당선, FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 5);
        역삼_영통_거리5_구간 = new Section(경의중앙선, FakeStationFactory.역삼역(), FakeStationFactory.영통역(), 5);

        전체구간 = List.of(강남_선릉_거리2_구간, 선릉_왕십리_거리10_구간, 선릉_왕십리_거리5_구간, 역삼_영통_거리5_구간);
        지하철_목록 = List.of(
                FakeStationFactory.강남역(), FakeStationFactory.선릉역(),
                FakeStationFactory.왕십리역(), FakeStationFactory.역삼역(), FakeStationFactory.영통역()
        );
    }

    @Test
    void 최단_경로를_조회한다() {
        //given
        PathFinder pathFinder = new PathFinder(new DijkstraPathFindStrategy(지하철_목록, 전체구간));

        //when
        List<Station> shortest = pathFinder.findShortestPath(FakeStationFactory.강남역(), FakeStationFactory.왕십리역());

        //then
        최단_경로_조회_검증(shortest);
    }

    @Test
    void 최단_경로의_거리_조회() {
        PathFinder pathFinder = new PathFinder(new DijkstraPathFindStrategy(지하철_목록, 전체구간));

        //when
        int shortestDistance = pathFinder.getShortestDistance(FakeStationFactory.강남역(), FakeStationFactory.왕십리역());

        //then
        최단_거리_조회_검증(shortestDistance);
    }

    @Test
    void 출발역과_목적지가_같을_경우() {
        //given
        PathFinder pathFinder = new PathFinder(new DijkstraPathFindStrategy(지하철_목록, 전체구간));
        Station 강남역 = FakeStationFactory.강남역();

        //then
        출발지와_목적지가_동일할_경우_실패_검증(pathFinder, 강남역);
    }

    @Test
    void 출발역과_목적지가_이어져있지_않은_경우() {
        //given
        PathFinder pathFinder = new PathFinder(new DijkstraPathFindStrategy(지하철_목록, 전체구간));
        Station 강남역 = FakeStationFactory.강남역();
        Station 역삼역 = FakeStationFactory.역삼역();

        //then
        출발지와_목적지가_이어지지_않음을_검증(pathFinder, 강남역, 역삼역);
    }

    private void 출발지와_목적지가_이어지지_않음을_검증(PathFinder path, Station 강남역, Station 역삼역) {
        assertThatThrownBy(() -> path.findShortestPath(강남역, 역삼역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.UNCONNECTED_PATH.getMessage());
        assertThatThrownBy(() -> path.getShortestDistance(강남역, 역삼역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.UNCONNECTED_PATH.getMessage());
    }

    private void 최단_거리_조회_검증(int shortestDistance) {
        assertThat(shortestDistance).isEqualTo(
                강남_선릉_거리2_구간.getDistanceIntValue()
                + 선릉_왕십리_거리5_구간.getDistanceIntValue());
    }

    private void 최단_경로_조회_검증 (List<Station> shortest) {
        assertThat(shortest.stream()
                           .map(Station::getName)
                           .collect(Collectors.toList())
        ).containsExactly("강남역", "선릉역", "왕십리역");
    }

    private void 출발지와_목적지가_동일할_경우_실패_검증(PathFinder path, Station station) {
        assertThatThrownBy(() -> path.getShortestDistance(station, station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.SAME_SOURCE_AND_DESTINATION.getMessage());

        assertThatThrownBy(() -> path.getShortestDistance(station, station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.SAME_SOURCE_AND_DESTINATION.getMessage());
    }

}
