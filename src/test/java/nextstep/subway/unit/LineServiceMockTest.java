package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);


    }

    @Test
    void addSection() {
        // given
        final Long 신분당선_id = 1L;
        final Long 강남역_id = 1L;
        final Long 양재역_id = 1L;
        when(lineRepository.findById(신분당선_id)).thenReturn(Optional.of(new Line("신분당선", "red")));
        when(stationService.findById(강남역_id)).thenReturn(new Station("강남역"));
        when(stationService.findById(양재역_id)).thenReturn(new Station("양재역"));

        // when
        lineService.addSection(신분당선_id, new SectionRequest(강남역_id, 양재역_id, 10));

        // then
        LineResponse lineResponse = lineService.findById(신분당선_id);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }
}
