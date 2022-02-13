package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        Line line = new Line(1L, "2호선", "green");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));

        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(역삼역);

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 3));

        // then
        LineResponse lineResponse = lineService.findById(1L);
        assertThat(lineResponse.getStations()).hasSize(2);

    }
}
