package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.EntityFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

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

        LineService lineService = new LineService(lineRepository, stationService);
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
}
