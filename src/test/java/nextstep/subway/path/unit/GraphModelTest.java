package nextstep.subway.path.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.domain.GraphModel;
import nextstep.subway.path.dto.Path;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class GraphModelTest {

    Station 강남역;
    Station 역삼역;
    Station 논현역;

    @BeforeEach
    public void setup() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        논현역 = new Station(3L, "논현역");

    }

    @DisplayName("[createGraphModel] graph를 생성한다.")
    @Test
    void createGraphModel_success() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var graphModel = new GraphModel(1L, 2L);

        // when
        graphModel.createGraphModel(Collections.singletonList(신분당선));

        // then
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = graphModel.getGraph();
        DefaultWeightedEdge edge = graph.getEdge(강남역.getId(), 역삼역.getId());

        assertAll(
                () -> assertTrue(graph.containsVertex(강남역.getId())),
                () -> assertTrue(graph.containsVertex(역삼역.getId())),
                () -> assertTrue(graph.containsEdge(강남역.getId(), 역삼역.getId())),
                () -> assertNotNull(edge),
                () -> assertEquals(graph.getEdgeWeight(edge), 5.0)
        );
    }

    @DisplayName("[createGraphModel] 출발역과 도착역이 같은 section을 가진 linelist는 예외를 발생시킨다.")
    @Test
    void createGraphModel_fail() {
        // given
        var 강남역_강남역_구간 = new Section(강남역, 강남역, 5L);
        var 구간들 = new Sections(List.of(강남역_강남역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.createGraphModel(Collections.singletonList(신분당선)))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[createGraphModel] Linelist가 비어있으면 예외가 발생한다.")
    @Test
    void createGraphModel_fail2() {
        // given
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.createGraphModel(List.of()))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[createGraphModel] Linelist의 Sections가 비어있으면 예외가 발생한다.")
    @Test
    void createGraphModel_fail3() {
        // given
        var 구간들 = new Sections(List.of());
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.createGraphModel(Collections.singletonList(신분당선)))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[findShortestPath] Path를 생성한다.")
    @Test
    void findShortestPath_success() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_리스트 = Collections.singletonList(신분당선);
        var graphModel = new GraphModel(1L, 2L);
        graphModel.createGraphModel(지하철_리스트);

        // when
        Path path = graphModel.findPath(지하철_리스트);

        // then
        assertAll(
                () -> assertNotNull(path),
                () -> assertEquals(5.0, path.getWeight()),
                () -> assertEquals(List.of(강남역, 역삼역), path.getStationList())
        );
    }

    @DisplayName("[findShortestPath] lineList가 비어있으면 예외가 발생한다.")
    @Test
    void findShortestPath_fail1() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_리스트 = Collections.singletonList(신분당선);
        var graphModel = new GraphModel(1L, 2L);
        graphModel.createGraphModel(지하철_리스트);

        // when & then
        assertAll(
                () -> assertThrows(PathException.class, () -> graphModel.findPath(List.of()))
                        .getMessage().equals(PATH_NOT_FOUND.getDescription())
        );
    }

    @DisplayName("[getStationList] lineList와 stationId를 통해 StationList를 생성한다.")
    @Test
    void getStationList_success() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_리스트 = Collections.singletonList(신분당선);
        var graphModel = new GraphModel(1L, 2L);

        // when
        var 생성된_StationList = graphModel.getStationList(지하철_리스트, List.of(강남역.getId(), 역삼역.getId()));

        // then
        assertAll(
                () -> assertThat(생성된_StationList).containsExactlyInAnyOrder(강남역, 역삼역)
        );
    }

    @DisplayName("[getStationList] lineList에 StationId가 없다면 예외가 발생한다.")
    @Test
    void getStationList_fail2() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_리스트 = Collections.singletonList(신분당선);
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        assertThrows(PathException.class, () -> graphModel.getStationList(지하철_리스트, List.of(강남역.getId(), 논현역.getId())))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[addSectionsToGraph] line을 graph의 Edge에 추가한다.")
    @Test
    public void addSectionsToGraph_success() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var graphModel = new GraphModel(1L, 2L);

        // when
        graphModel.addSectionsToGraph(신분당선);

        // then
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = graphModel.getGraph();
        DefaultWeightedEdge edge = graph.getEdge(강남역.getId(), 역삼역.getId());

        assertAll(
                () -> assertTrue(graph.containsVertex(강남역.getId())),
                () -> assertTrue(graph.containsVertex(역삼역.getId())),
                () -> assertNotNull(edge),
                () -> assertEquals(graph.getEdgeWeight(edge), 5.0),
                () -> assertTrue(graph.containsEdge(강남역.getId(), 역삼역.getId()))
        );
    }

    @DisplayName("[addSectionsToGraph] Sections가 비어 있는 Line을 graph의 Edge에 추가하면 예외가 발생한다.")
    @Test
    public void addSectionsToGraph_fail() {
        // given
        var 빈_구간들 = new Sections(List.of());
        var 빈_구간을_가진_신분당선 = new Line(1L, "신분당선", "red", 15L, 빈_구간들);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.addSectionsToGraph(빈_구간을_가진_신분당선))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[addSectionsToGraph] 동일한 StationId를 가지고 있는 Section을 graph의 Edge에 추가하면 예외가 발생한다.")
    @Test
    public void addSectionsToGraph_fail2() {
        // given
        var 강남역_역삼역_구간 = new Section(강남역, 강남역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.addSectionsToGraph(신분당선))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[addEdge] 새로운 Edge를 생성한다.")
    @Test
    public void addEdge_success() {
        // given
        var graphModel = new GraphModel(1L, 2L);

        graphModel.addEdge(강남역.getId(), 역삼역.getId(), 20.0);

        // then
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = graphModel.getGraph();
        DefaultWeightedEdge edge = graph.getEdge(강남역.getId(), 역삼역.getId());

        assertAll(
                () -> assertTrue(graph.containsVertex(강남역.getId())),
                () -> assertTrue(graph.containsVertex(역삼역.getId())),
                () -> assertTrue(edge != null),
                () -> assertTrue(graph.getEdgeWeight(edge) == 20.0)
        );
    }

    @DisplayName("[addEdge] 동일한 source와 target으로는 Edge를 생성할 수 없다.")
    @Test
    public void addEdge_fail() {
        // given
        var graphModel = new GraphModel(1L, 2L);

        // then
        Assertions.assertThrows(PathException.class, () -> graphModel.addEdge(4L, 4L, 20.0))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[validateDuplicate] 동일하지 않는 source와 target을 인자로 주면 예외가 발생하지 않는다.")
    @Test
    public void validateDuplicate_success() {
        // give
        var source = 1L;
        var target = 2L;
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertDoesNotThrow(() -> graphModel.validateDuplicate(source, target));
    }

    @DisplayName("[validateDuplicate] 동일한 source와 target을 인자로 주면 예외가 발생한다.")
    @Test
    public void validateDuplicate_fail() {
        // give
        var source = 1L;
        var target = 1L;
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.validateDuplicate(source, target))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[getStation] stationId에 해당하는 Station을 찾는다. upStation으로 찾는다.")
    @Test
    void getStation_success() {
        // when
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_목록 = List.of(신분당선);
        var graphModel = new GraphModel(1L, 2L);

        var 찾은_역 = graphModel.getStation(지하철_목록, 강남역.getId());

        // then
        assertAll(
                () -> assertEquals(찾은_역, 강남역)
        );
    }

    @DisplayName("[getStation] stationId에 해당하는 Station을 찾는다. downStation으로 찾는다.")
    @Test
    void getStation_success2() {
        // when
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_목록 = List.of(신분당선);
        var graphModel = new GraphModel(1L, 2L);

        var 찾은_역 = graphModel.getStation(지하철_목록, 역삼역.getId());

        // then
        assertAll(
                () -> assertEquals(찾은_역, 역삼역)
        );
    }

    @DisplayName("[getStation] stationId에 해당하는 Station을 찾지 못하면 예외가 발생한다.")
    @Test
    void getStation_fail1() {
        // when
        var 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5L);
        var 구간들 = new Sections(List.of(강남역_역삼역_구간));
        var 신분당선 = new Line(1L, "신분당선", "red", 15L, 구간들);
        var 지하철_목록 = List.of(신분당선);
        var graphModel = new GraphModel(1L, 2L);

        // then
        assertThrows(PathException.class, () -> graphModel.getStation(지하철_목록, 3L))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[getStation] lineList가 비어 있으면 예외가 발생한다.")
    @Test
    void getStation_fail2() {
        // when
        var graphModel = new GraphModel(1L, 2L);

        // when & then
        assertThrows(PathException.class, () -> graphModel.getStation(List.of(), 3L))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }
}
