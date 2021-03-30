package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.path.domain.StationGraphPath;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("PathService 단위테스트")
@SpringBootTest
@Transactional
public class PathServiceTest {
    @Autowired
    private PathService pathService;
    @Autowired
    private PathFinder pathFinder;
    @Autowired
    private GraphService graphService;

    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    private Section 강남_양재;
    private Section 교대_강남;
    private Section 교대_남부;
    private Section 남부_양재;

    @BeforeEach
    void setup() {
        강남역 = new Station(1L,"강남역");
        양재역 = new Station(2L,"양재역");
        교대역 = new Station(3L,"교대역");
        남부터미널역 = new Station(4L,"남부터미널역");

        강남_양재 = new Section(null, 강남역, 양재역, 10);
        교대_강남 = new Section(null, 교대역, 강남역, 10);
        교대_남부 = new Section(null, 교대역, 남부터미널역, 3);
        남부_양재 = new Section(null, 남부터미널역, 양재역, 2);
    }

    @DisplayName("최단거리 조회")
    @Test
    void getShortedPath() {
        StationGraphPath path = pathFinder.getShortestPath(
                Arrays.asList(강남_양재, 교대_강남, 교대_남부, 남부_양재),
                강남역,
                남부터미널역);

        assertThat(path.getVertexStations()).isEqualTo(Arrays.asList(강남역, 양재역, 남부터미널역));
        assertThat(path.getDistance()).isEqualTo(12);
    }

    @DisplayName("같은 역으로 path 조회")
    @Test
    void getShortedPathWithSameStation() {
        assertThatExceptionOfType(RuntimeException.class).
                isThrownBy(()->pathService.getPath(강남역.getId(),강남역.getId()));
    }

    @DisplayName("SubwayGraph 생성 테스트")
    @Test
    void makeSubwayGraph() {
        List<Section> sections = Arrays.asList(교대_강남,강남_양재);
        List<Station> stations = Arrays.asList(교대역,강남역,양재역);

        SubwayGraph subwayGraph = graphService.findGraph(sections,stations);

        assertThat(subwayGraph.getSubwayGraph().edgeSet().size()).isEqualTo(2);
    }

}
