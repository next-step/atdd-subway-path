package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.fixture.LineFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;

class DijkstraPathFinderTest {

    public static final long STATION_ID_1 = 0L;
    public static final long STATION_ID_2 = 1L;
    public static final long STATION_ID_3 = 2L;
    public static final long STATION_ID_4 = 3L;
    private Line line2;
    private Line line;
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
        line = LineFixture.createLineWithSection(STATION_ID_1, STATION_ID_4);
        line.addSection(createSection(STATION_ID_4, STATION_ID_3, 5));

        line2 = LineFixture.createLineWithSection(STATION_ID_1, STATION_ID_2);
        line2.addSection(createSection(STATION_ID_2, STATION_ID_3, 2));

        Lines lines = Lines.from(Set.of(line, line2));
        sectionList = lines.mergeSections();
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
}