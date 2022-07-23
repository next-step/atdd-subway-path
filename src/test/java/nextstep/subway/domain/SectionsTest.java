package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    @DisplayName("지하철 구간 중간에 새로운 역을 추가 - 상행 종점 기준")
    @Test
    void addSectionFocusUpStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 10));

        // when
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 3));

        // then
        assertStations(sections, Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    @DisplayName("지하철 구간 중간에 새로운 역을 추가 - 하행행 종점 기준")
    @Test
    void addSectionFocusDownStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 10));

        // when
        sections.add(new Section(Stub.신대방역, Stub.신림역, 6));

        // then
        assertStations(sections, Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    @DisplayName("새로운 상행 종점역 추가")
    @Test
    void addNewUpStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 10));

        // when
        sections.add(new Section(Stub.대림역, Stub.구로디지털단지역, 4));

        // then
        assertStations(sections, Stub.대림역, Stub.구로디지털단지역, Stub.신대방역);
    }

    @DisplayName("새로운 하행 종점역 추가")
    @Test
    void addNewDownStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));

        // when
        sections.add(new Section(Stub.신대방역, Stub.신림역, 6));

        // then
        assertStations(sections, Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    @DisplayName("지하철 구간 중간에 새로운 역을 추가할 때 거리가 같은 경우 예외")
    @Test
    void doNotSameDistanceAddSection() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 10));

        // then
        assertThatThrownBy(() -> sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void doNotDuplicatedStations() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 10));

        // then
        assertThatThrownBy(() -> sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void doNotUnknownStations() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신림역, 10));

        // then
        assertThatThrownBy(() -> sections.add(new Section(Stub.대림역, Stub.신대방역, 6)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간에서 하행 종점역 삭제")
    @Test
    void deleteLastStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));
        sections.add(new Section(Stub.신대방역, Stub.신림역, 7));

        // when
        sections.remove(Stub.신림역);

        // then
        assertStations(sections, Stub.구로디지털단지역, Stub.신대방역);
    }

    @DisplayName("지하철 구간에서 상행 종점역 삭제")
    @Test
    void deleteFirstStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));
        sections.add(new Section(Stub.신대방역, Stub.신림역, 7));

        // when
        sections.remove(Stub.구로디지털단지역);

        // then
        assertStations(sections, Stub.신대방역, Stub.신림역);
    }

    @DisplayName("지하철 구간에서 중간 지하철역 삭제")
    @Test
    void deleteBetweenStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));
        sections.add(new Section(Stub.신대방역, Stub.신림역, 7));

        // when
        sections.remove(Stub.신대방역);

        // then
        assertStations(sections, Stub.구로디지털단지역, Stub.신림역);
    }

    @DisplayName("지하철 구간이 하나인 경우 지하철역을 삭제하면 예외 발생")
    @Test
    void deleteStationAtOneSection() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));

        // then
        assertThatThrownBy(() -> sections.remove(Stub.신대방역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간이 비어있는데 지하철역을 삭제하면 예외 발생")
    @Test
    void deleteStationAtEmptySections() {
        // given
        Sections sections = new Sections();

        // then
        assertThatThrownBy(() -> sections.remove(Stub.신대방역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간에 없는 지하철역을 삭제하면 예외 발생")
    @Test
    void deleteUnknownStationInSections() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));

        // then
        assertThatThrownBy(() -> sections.remove(Stub.대림역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("그래프에 지하철 구간 정보 추가")
    @Test
    void addSectionsToGraph() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(Stub.구로디지털단지역, Stub.신대방역, 4));
        sections.add(new Section(Stub.신대방역, Stub.신림역,  8));

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex(Stub.구로디지털단지역);
        graph.addVertex(Stub.신대방역);
        graph.addVertex(Stub.신림역);

        // when
        sections.addSectionsToGraph(graph);

        // then
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<Station> paths = dijkstraShortestPath.getPath(Stub.구로디지털단지역, Stub.신림역).getVertexList();
        assertThat(paths).containsExactly(Stub.구로디지털단지역, Stub.신대방역, Stub.신림역);
    }

    private void assertStations(Sections sections, Station... actualStations) {
        assertThat(sections.getStations()).containsExactly(actualStations);
    }

    private static class Stub {
        public static final Station 대림역 = new Station("대림역");
        public static final Station 구로디지털단지역 = new Station("구로디지털단지역");
        public static final Station 신대방역 = new Station("신대방역");
        public static final Station 신림역 = new Station("신림역");
    }
}