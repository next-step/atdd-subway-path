package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
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
    private Long station1Id = 11L;
    private Long station2Id = 12L;
    private Long station3Id = 13L;
    private Long station4Id = 14L;

    @BeforeEach
    void init() {

        line1 = spy(Line.class);
        line2 = spy(Line.class);
        line3 = spy(Line.class);

        station1 = spy(Station.class);
        station2 = spy(Station.class);
        station3 = spy(Station.class);
        station4 = spy(Station.class);

    }

    /**
     *
     * line1: station1 - (9) - station2
     * line2: station2 - (2) - station3
     * line3: station3 - (10) - station4 - (11) - station1
     *
     * WHEN station1에서 station3으로 가는 경로 조회하면
     * THEN: station1-station2-station3으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 1")
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
     * WHEN station1에서 station3으로 가는 경로 조회하면
     * THEN: station1-station2-station3으로 경로가 나온다.
     */
    @Test
    @DisplayName("경로 조회 테스트 2")
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


    private List<Section> getAllSections() {
        List<Section> allSections = new ArrayList<>();
        allSections.addAll(line1.getSections());
        allSections.addAll(line2.getSections());
        allSections.addAll(line3.getSections());
        return allSections;
    }
}