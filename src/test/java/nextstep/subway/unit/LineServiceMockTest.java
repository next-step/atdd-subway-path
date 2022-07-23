package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    final Long 신분당선_id = 1L;
    final String 신분당선_name = "신분당선";
    final Long 강남역_id = 1L;
    final Long 양재역_id = 1L;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        when(lineRepository.findById(신분당선_id)).thenReturn(Optional.of(new Line(신분당선_name, "red")));
        when(stationService.findStation(강남역_id)).thenReturn(new Station("강남역"));
        when(stationService.findStation(양재역_id)).thenReturn(new Station("양재역"));

        // when
        lineService.addSection(신분당선_id, new SectionRequest(강남역_id, 양재역_id, 10));

        // then
        LineResponse lineResponse = lineService.findLineResponse(신분당선_id);
        assertThat(lineResponse.getName()).isEqualTo(신분당선_name);
    }
}
