package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.exception.CanNotFindShortestPathException;
import nextstep.subway.fixture.LineFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DijkstraPathFinderTest {

    private static final long STATION_ID_1 = 0L;
    private static final long STATION_ID_2 = 1L;
    private static final long STATION_ID_3 = 2L;
    private static final long STATION_ID_4 = 3L;
    private static final long STATION_ID_10 = 9L;
    private static final long STATION_ID_11 = 10L;

    private List<Section> sectionList;

    /**
     * 역1 - 노선1(10) - 역4
     * |               |
     * 노선2(10)     노선1(5)
     * |               |
     * 역2 - 노선2(2) -  역3
     * <p>
     * 최단거리 역1 -> 역2 -> 역3 = 12
     */
    @BeforeEach
    void setUp() {
        Line line = LineFixture.createLineWithSection(STATION_ID_1, STATION_ID_4);
        line.addSection(createSection(STATION_ID_4, STATION_ID_3, 5));

        Line line2 = LineFixture.createLineWithSection(STATION_ID_1, STATION_ID_2);
        line2.addSection(createSection(STATION_ID_2, STATION_ID_3, 2));

        Lines lines = Lines.from(Set.of(line, line2));
        sectionList = lines.mergeSections();

        LineFixture.createLineWithSection(STATION_ID_10, STATION_ID_11);
    }

    @Test
    void 최단거리_조회() {
        //given
        PathRequest pathRequest = PathRequest.of(STATION_ID_1, STATION_ID_3);
        PathFinder finder = new DijkstraPathFinder();

        //when
        Path path = finder.searchShortestPath(pathRequest, sectionList);

        //then
        assertThat(path.getStations()).hasSize(3);
        assertThat(path.getStations().stream().map(s -> s.getId())).containsExactly(STATION_ID_1, STATION_ID_2, STATION_ID_3);
        assertThat(path.getDistance()).isEqualTo(12);
    }

    @Test
    void 출발역과_도착역이_같으면_조회불가() {
        //given
        PathRequest pathRequest = PathRequest.of(STATION_ID_1, STATION_ID_1);
        PathFinder finder = new DijkstraPathFinder();

        //when & then
        assertThatThrownBy(() -> finder.searchShortestPath(pathRequest, sectionList))
                .isInstanceOf(CanNotFindShortestPathException.class);

    }

    @Test
    void 출발역과_도착역이_연결되지_않으면_조회불가() {
        //given
        PathRequest pathRequest = PathRequest.of(STATION_ID_1, STATION_ID_10);
        PathFinder finder = new DijkstraPathFinder();

        //when & then
        assertThatThrownBy(() -> finder.searchShortestPath(pathRequest, sectionList))
                .isInstanceOf(CanNotFindShortestPathException.class);
    }

    @Test
    void 출발역_또는_도착역이_없으면_조회불가() {
        //given
        PathRequest pathRequest = PathRequest.of(999L, 1000L);
        PathFinder finder = new DijkstraPathFinder();

        //when & then
        assertThatThrownBy(() -> finder.searchShortestPath(pathRequest, sectionList))
                .isInstanceOf(CanNotFindShortestPathException.class);
    }
}