package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import nextstep.subway.line.application.LineSectionService;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @InjectMocks
    private LineService lineService;
    @InjectMocks
    private LineSectionService lineSectionService;

    @Test
    void addSection() {
        // given
        Long 신분당선_id = 1L;
        Long 신사역_id = 1L;
        Long 논현역_id = 2L;
        Long 신논현역_id = 3L;

        when(stationRepository.findByIdOrThrow(신사역_id)).thenReturn(new Station("신사역"));
        when(stationRepository.findByIdOrThrow(논현역_id)).thenReturn(new Station("논현역"));
        when(stationRepository.findByIdOrThrow(신논현역_id)).thenReturn(new Station("신논현역"));
        when(lineRepository.findByIdOrThrow(신분당선_id)).thenReturn(
            new Line("신분당선", "red", new LineSections()));
        lineSectionService.saveSection(신분당선_id, new SectionRequest(신사역_id, 논현역_id, 10L));

        // when
        lineSectionService.saveSection(신분당선_id, new SectionRequest(논현역_id, 신논현역_id, 10L));

        // then
        LineResponse lineResponse = lineService.findLine(신분당선_id);
        assertThat(lineResponse.getStations().stream()
            .map(StationResponse::getName))
            .contains("신사역", "논현역", "신논현역");
    }
}
