package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.path.SubwayMap;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubwayMapTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역 --- 거리: 10, *2호선* --- 강남역
     * |                              |
     * 거리: 2, *3호선*               거리: 10, *신분당선*
     * |                                 |
     * 남부터미널역 --- 거리: 3, *3호선* --- 양재역
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(new Section(신분당선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(신분당선, 남부터미널역, 양재역, 3));
    }

    @DisplayName("전체 지하철 노선도를 그린다.")
    @Test
    void subwayMap() {
        // Given
        List<Line> lines = List.of(이호선, 신분당선, 삼호선);

        // When
        SubwayMap subwayMap = new SubwayMap(lines, new WeightedMultigraph<>(DefaultWeightedEdge.class));

        // Then
        assertThat(subwayMap.getMap().vertexSet()).containsOnly(강남역, 교대역, 양재역, 남부터미널역);
    }
}
