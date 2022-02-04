package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
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

    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));
        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(역삼역);
        LineService lineService = new LineService(lineRepository, stationService);
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);

        // when
        lineService.addSection(1L, sectionRequest);

        // then
        LineResponse line = lineService.findById(1L);
        assertThat(line.getStations()).hasSize(2);
    }
}
