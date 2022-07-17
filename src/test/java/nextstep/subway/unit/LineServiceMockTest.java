package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
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
        final long lineId = 1L;
        final long upStationId = 1L;
        final long downStationId = 2L;

        Line line = new Line("2호선", "bg-green-600");
        doReturn(Optional.of(line)).when(lineRepository).findById(lineId);

        Station gangnam = new Station("강남역");
        Station yeoksam = new Station("역삼역");
        doReturn(gangnam).when(stationService).findById(upStationId);
        doReturn(yeoksam).when(stationService).findById(downStationId);

        // when
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 10);
        lineService.addSection(lineId, sectionRequest);

        // then
        assertThat(line.getStations()).containsOnly(gangnam, yeoksam);
    }
}
