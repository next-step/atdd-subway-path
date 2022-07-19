package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Line line = new Line("신분당선", "red");

        when(stationService.findById(1L)).thenReturn(new Station("강남"));
        when(stationService.findById(2L)).thenReturn(new Station("양재"));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 6));

        // then
        assertThat(line.getSections()).hasSize(1);
    }
}
