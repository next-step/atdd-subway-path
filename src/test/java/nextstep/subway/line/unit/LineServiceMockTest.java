package nextstep.subway.line.unit;

import nextstep.subway.line.controller.dto.LineResponse;
import nextstep.subway.line.controller.dto.SectionAddRequest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.controller.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.service.StationService;
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
    private StationRepository stationRepository;

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private LineService lineService;

    private long LINE_ID = 1L;
    private long UP_STATION_ID = 1L;
    private long DOWN_STATION_ID = 2L;

    @BeforeEach
    void setup() {
        when(lineRepository.findById(LINE_ID))
            .thenReturn(Optional.of(Line.of("2호선", "green")));

        when(stationRepository.findById(UP_STATION_ID))
            .thenReturn(Optional.of(new Station("강남역")));
        when(stationRepository.findById(DOWN_STATION_ID))
            .thenReturn(Optional.of(new Station("역삼역")));
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        SectionAddRequest sectionAddRequest = new SectionAddRequest(UP_STATION_ID, DOWN_STATION_ID, 2);

        // when
        // lineService.addSection 호출
        lineService.addSection(LINE_ID, sectionAddRequest);

        // then
        // lineService.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.getLine(LINE_ID);
        assertThat(lineResponse).isNotNull();
        assertThat(lineResponse.getStations()).isNotEmpty();
        assertThat(lineResponse.getStations().stream().map(StationResponse::getName))
            .contains("강남역", "역삼역");
    }
}
