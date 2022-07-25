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
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        doReturn(new Station("강남역"))
                .when(stationService)
                .findById(1L);
        doReturn(new Station("역삼역"))
                .when(stationService)
                .findById(2L);

        Line line = new Line("2호선", "빨간색");
        doReturn(Optional.of(line))
                .when(lineRepository)
                .findById(1L);

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        assertThat(line.getSections()).isNotEmpty();
    }
}
