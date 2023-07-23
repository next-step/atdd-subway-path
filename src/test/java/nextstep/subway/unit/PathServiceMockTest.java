package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private StationService stationService;
    @Mock
    private SectionService sectionService;

    @InjectMocks
    private PathService pathService;

    private Line line1;
    private Line line2;
    private Line line3;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
    private Station station7;
    private Long station1Id = 11L;
    private Long station2Id = 12L;
    private Long station3Id = 13L;
    private Long station4Id = 14L;
    private Long station5Id = 15L;
    private Long station6Id = 16L;
    private Long station7Id = 17L;

    @BeforeEach
    void init() {

        line1 = spy(Line.class);
        line2 = spy(Line.class);
        line3 = spy(Line.class);

        station1 = spy(Station.class);
        station2 = spy(Station.class);
        station3 = spy(Station.class);
        station4 = spy(Station.class);
        station5 = spy(Station.class);
        station6 = spy(Station.class);
        station7 = spy(Station.class);

    }

    /**
     *
     * line1: station1 - (9) - station2
     * line2: station2 - (2) - station3
     * line3: station3 - (10) - station4 - (11) - station1
     *
     * GIVEN 정점 4개, 간선 4개
     * WHEN station1에서 station3으로 가는 경로 조회하면
     * THEN station1-station2-station3으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 1 (환승 있는 경우1) - 정점 4개, 간선 4개")
    void searchPath1() {

        // given
        when(station1.getId()).thenReturn(station1Id);
        when(station2.getId()).thenReturn(station2Id);
        when(station3.getId()).thenReturn(station3Id);

        line1.addSection(station1, station2, 9);
        line2.addSection(station2, station3, 2);
        line3.addSection(station3, station4, 10);
        line3.addSection(station4, station1, 11);

        List<Section> allSections = getAllSections();

        when(stationService.findById(station1Id)).thenReturn(station1);
        when(stationService.findById(station3Id)).thenReturn(station3);
        when(sectionService.findAllSections()).thenReturn(allSections);

        // when
        PathResponse pathResponse = pathService.searchPath(station1Id, station3Id);

        // then
        assertThat(pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsExactly(station1Id, station2Id, station3Id);
    }

    /**
     *
     * line1: station1 - (22) - station2
     * line2: station2 - (15) - station3
     * line3: station3 - (10) - station4 - (11) - station1
     *
     * GIVEN 정점 4개, 간선 4개
     * WHEN station1에서 station3으로 가는 경로 조회하면
     * THEN: station1-station2-station3으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 2 (환승 있는 경우2) - 정점 4개, 간선 4개")
    void searchPath2() {

        // given
        when(station1.getId()).thenReturn(station1Id);
        when(station3.getId()).thenReturn(station3Id);
        when(station4.getId()).thenReturn(station4Id);

        line1.addSection(station1, station2, 22);
        line2.addSection(station2, station3, 15);
        line3.addSection(station3, station4, 10);
        line3.addSection(station4, station1, 11);

        List<Section> allSections = getAllSections();

        when(stationService.findById(station1Id)).thenReturn(station1);
        when(stationService.findById(station3Id)).thenReturn(station3);
        when(sectionService.findAllSections()).thenReturn(allSections);

        // when
        PathResponse pathResponse = pathService.searchPath(station1Id, station3Id);

        // then
        assertThat(pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsExactly(station1Id, station4Id, station3Id);
    }

    /**
     *
     * line1: station1 - (10) - station2 - (9) - station3
     *
     * GIVEN 정점 3개, 간선 2개
     * WHEN station1에서 station3으로 가는 경로 조회하면
     * THEN: station1-station2-station3으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 3 (환승 없는 경우) - 정점 3개, 간선 2개")
    void searchPath3() {

        // given
        when(station1.getId()).thenReturn(station1Id);
        when(station2.getId()).thenReturn(station2Id);
        when(station3.getId()).thenReturn(station3Id);

        line1.addSection(station1, station2, 10);
        line1.addSection(station2, station3, 9);

        List<Section> allSections = getAllSections();

        when(stationService.findById(station1Id)).thenReturn(station1);
        when(stationService.findById(station3Id)).thenReturn(station3);
        when(sectionService.findAllSections()).thenReturn(allSections);

        // when
        PathResponse pathResponse = pathService.searchPath(station1Id, station3Id);

        // then
        assertThat(pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsExactly(station1Id, station2Id, station3Id);
    }

    /**
     *
     * line1: station1 - (2) - station2 - (3) - station3 - (3) - station4
     * line2: station5 - (7) - station2 - (2)  - station6 - (2) - station7 - (4) - station4
     *
     * GIVEN 정점 7개, 간선 7개
     * WHEN station1에서 station4으로 가는 경로 조회하면
     * THEN: station1-station2-station3-station4으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 4 (환승 없는게 빠른 경우) - 정점 7개, 간선 7개")
    void searchPath4() {

        // given
        when(station1.getId()).thenReturn(station1Id);
        when(station2.getId()).thenReturn(station2Id);
        when(station3.getId()).thenReturn(station3Id);
        when(station4.getId()).thenReturn(station4Id);

        line1.addSection(station1, station2, 2);
        line1.addSection(station2, station3, 3);
        line1.addSection(station3, station4, 3);

        line2.addSection(station5, station2, 7);
        line2.addSection(station2, station6, 2);
        line2.addSection(station6, station7, 2);
        line2.addSection(station7, station4, 4);

        List<Section> allSections = getAllSections();

        when(stationService.findById(station1Id)).thenReturn(station1);
        when(stationService.findById(station4Id)).thenReturn(station4);
        when(sectionService.findAllSections()).thenReturn(allSections);

        // when
        PathResponse pathResponse = pathService.searchPath(station1Id, station4Id);

        // then
        assertThat(pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsExactly(station1Id, station2Id, station3Id, station4Id);
    }

    /**
     *
     * line1: station1 - (12) - station2 - (10) - station3 - (20) - station4
     * line2: station5 - (7) - station2 - (2)  - station6 - (2) - station7 - (4) - station4
     *
     * GIVEN 정점 7개, 간선 7개
     * WHEN station1에서 station4으로 가는 경로 조회하면
     * THEN: station1-station2-station6-station7-station4으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 5 (환승 하는게 빠른 경우) - 정점 7개, 간선 7개")
    void searchPath5() {

        // given
        when(station1.getId()).thenReturn(station1Id);
        when(station2.getId()).thenReturn(station2Id);
        when(station4.getId()).thenReturn(station4Id);
        when(station6.getId()).thenReturn(station6Id);
        when(station7.getId()).thenReturn(station7Id);

        line1.addSection(station1, station2, 12);
        line1.addSection(station2, station3, 10);
        line1.addSection(station3, station4, 20);

        line2.addSection(station5, station2, 7);
        line2.addSection(station2, station6, 2);
        line2.addSection(station6, station7, 2);
        line2.addSection(station7, station4, 4);

        List<Section> allSections = getAllSections();

        when(stationService.findById(station1Id)).thenReturn(station1);
        when(stationService.findById(station4Id)).thenReturn(station4);
        when(sectionService.findAllSections()).thenReturn(allSections);

        // when
        PathResponse pathResponse = pathService.searchPath(station1Id, station4Id);

        // then
        assertThat(pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsExactly(station1Id, station2Id, station6Id, station7Id, station4Id);
    }


    private List<Section> getAllSections() {
        List<Section> allSections = new ArrayList<>();
        allSections.addAll(line1.getSections());
        allSections.addAll(line2.getSections());
        allSections.addAll(line3.getSections());
        return allSections;
    }
}