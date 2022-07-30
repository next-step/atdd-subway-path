package nextstep.subway.unit;

import nextstep.subway.domain.*;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PathTest {

    private Line 분당선;
    private Line 신분당선;
    private Section 강남_선릉_거리2_구간;
    private Section 선릉_왕십리_거리10_구간;
    private Section 선릉_왕십리_거리5_구간;

    private List<Section> 전체구간;
    private List<Station> 지하철_목록;


    @BeforeEach
    void setUp() {
        분당선 = FakeLineFactory.분당선();
        신분당선 = FakeLineFactory.신분당선();

        강남_선릉_거리2_구간 = new Section(분당선, FakeStationFactory.강남역(), FakeStationFactory.선릉역(), 2);
        선릉_왕십리_거리10_구간 = new Section(분당선, FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 10);
        선릉_왕십리_거리5_구간 = new Section(신분당선, FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 5);

        전체구간 = List.of(강남_선릉_거리2_구간, 선릉_왕십리_거리10_구간, 선릉_왕십리_거리5_구간);
        지하철_목록 = List.of(FakeStationFactory.강남역(), FakeStationFactory.선릉역(), FakeStationFactory.왕십리역());
    }


    @Test
    void 최단_경로를_조회한다() {
        //given
        Path path = new Path(지하철_목록, 전체구간);

        //when
        List<Station> shortest = path.getShortestWithDijkstra(FakeStationFactory.강남역(), FakeStationFactory.왕십리역());

        //then
        최단_경로_조회_검증(shortest);
    }

    @Test
    void 최단_경로의_거리_조회() {
        Path path = new Path(지하철_목록, 전체구간);

        //when
        int shortestDistance = path.getShortestDistance(FakeStationFactory.강남역(), FakeStationFactory.왕십리역());

        //then
        최단_거리_조회_검증(shortestDistance);
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

}
