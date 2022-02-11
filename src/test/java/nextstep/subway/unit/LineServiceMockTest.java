package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.ShortestPathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.utils.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest extends ShortestPathTestableLinesFixture {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    private ShortestPathFinder<Station, Line, Integer> shortestPathFinder = new LinesJGraphShortestPathFinder();

    private final static Long 노선ID = 1L;
    private final static Long 상행종점역ID = 1L;
    private final static Long 하행종점역ID = 2L;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line 노선 = new Line("2호선", "green");
        Station 상행종점역 = EntityFixtures.createEntityFixtureWithId(상행종점역ID, Station.class);
        Station 하행종점역 = EntityFixtures.createEntityFixtureWithId(하행종점역ID, Station.class);

        when(stationService.findById(상행종점역ID)).thenReturn(상행종점역);
        when(stationService.findById(하행종점역ID)).thenReturn(하행종점역);
        when(lineRepository.findById(노선ID)).thenReturn(Optional.of(노선));

        LineService lineService = new LineService(lineRepository, stationService, shortestPathFinder);
        SectionRequest sectionRequest = new SectionRequest(상행종점역ID, 하행종점역ID, 100);

        // when
        // lineService.addSection 호출
        lineService.addSection(노선ID, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(노선ID);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
        assertThat(lineResponse.getStations().stream().map(StationResponse::getId)).containsExactly(상행종점역ID, 하행종점역ID);
    }

    @Test
    void findShortestPath() {
        // given
        LineService lineService = new LineService(lineRepository, stationService, shortestPathFinder);
        Station 출발역 = 강남역;
        Station 도착역 = 양재역;
        when(stationService.findById(출발역.getId())).thenReturn(출발역);
        when(stationService.findById(도착역.getId())).thenReturn(도착역);
        when(lineRepository.findAll()).thenReturn(lines);

        // when
        ShortestPathResponse shortestPathResponse = lineService.findShortestPath(출발역.getId(), 도착역.getId());

        // then
        assertThat(shortestPathResponse.getStations().stream().map(StationResponse::getId)).containsExactly(2L, 3L, 6L, 5L);
        assertThat(shortestPathResponse.getDistance()).isEqualTo(1100);
    }


}
