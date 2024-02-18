package nextstep.subway.unit;

import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private LineService lineService;

    private Station 강남역;
    private Station 역삼역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        이호선 = new Line("2호선", "green", 10, 강남역, 역삼역);
    }

    @Test
    void addSection() {
        // given
        when(stationService.findStation(1L)).thenReturn(강남역);
        when(stationService.findStation(2L)).thenReturn(역삼역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        SectionRequest request = new SectionRequest(2L, 1L, 10);

        // when
        lineService.createSection(이호선.getId(), request);

        // then
        assertThat(이호선.getSections().getStations()).hasSize(2);
    }
}
