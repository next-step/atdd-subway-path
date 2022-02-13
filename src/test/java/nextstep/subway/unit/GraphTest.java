package nextstep.subway.unit;

import nextstep.subway.domain.Graph;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class GraphTest {

    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    private Section 교대역에서강남역;
    private Section 강남역에서양재역;
    private Section 교대역에서남부터미널역;
    private Section 남부터미널역에서양재역;
    private List<Line> 모든라인;

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */
    @BeforeEach
    public void setUp() {

        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");

        ReflectionTestUtils.setField(교대역, "id", 1L);
        ReflectionTestUtils.setField(강남역, "id", 2L);
        ReflectionTestUtils.setField(양재역, "id", 3L);
        ReflectionTestUtils.setField(남부터미널역, "id", 4L);

        이호선 = new Line("이호선", "green");
        교대역에서강남역 = new Section(이호선, 교대역, 강남역, 10);
        이호선.initSection(교대역에서강남역);

        신분당선 = new Line("신분당선", "red");
        강남역에서양재역 = new Section(신분당선, 강남역, 양재역, 10);
        신분당선.initSection(강남역에서양재역);

        삼호선 = new Line("삼호선", "orange");
        교대역에서남부터미널역 = new Section(삼호선, 교대역, 남부터미널역, 2);
        삼호선.initSection(교대역에서남부터미널역);

        남부터미널역에서양재역 = new Section(삼호선, 남부터미널역, 양재역, 3);
        삼호선.addSection(남부터미널역에서양재역);

        모든라인 = new ArrayList<>();
        모든라인.add(이호선);
        모든라인.add(삼호선);
        모든라인.add(신분당선);


        ReflectionTestUtils.setField(신분당선, "id", 1L);
        ReflectionTestUtils.setField(이호선, "id", 2L);
        ReflectionTestUtils.setField(삼호선, "id", 3L);

        ReflectionTestUtils.setField(교대역에서강남역, "id", 1L);
        ReflectionTestUtils.setField(강남역에서양재역, "id", 2L);
        ReflectionTestUtils.setField(교대역에서남부터미널역, "id", 3L);
        ReflectionTestUtils.setField(남부터미널역에서양재역, "id", 4L);
    }

    @Test
    public void parseAllSections() {
        Graph graph = new Graph(모든라인);
        assertThat(graph.getAllSections()).contains(교대역에서강남역, 강남역에서양재역, 교대역에서남부터미널역);
    }

    @Test
    public void parseAllStations() {
        Graph graph = new Graph(모든라인);
        assertThat(graph.getAllStations()).contains(강남역, 남부터미널역, 양재역, 교대역);
    }

    @Test
    public void 강남역에서_교대역_최단경로() {
        Graph graph = new Graph(모든라인);
        assertThat(graph.dijkstraShortestPath(강남역.getId(), 교대역.getId())).isEqualTo(Arrays.asList(2L, 1L));
    }

    @Test
    public void 강남역에서_남부터미널역_최단거리() {
        Graph graph = new Graph(모든라인);
        List<Long> path = graph.dijkstraShortestPath(강남역.getId(), 남부터미널역.getId());
        assertThat(graph.dijkstraShortestDistance(path)).isEqualTo(12);
    }

    @Test
    public void 강남역에서_교대역_최단거리() {
        Graph graph = new Graph(모든라인);
        List<Long> path = graph.dijkstraShortestPath(강남역.getId(), 교대역.getId());
        assertThat(graph.dijkstraShortestDistance(path)).isEqualTo(10);
    }
}
