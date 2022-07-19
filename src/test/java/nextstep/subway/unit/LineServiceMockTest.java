package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private final int DEFAULT_DISTANCE = 10;

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {

        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("1호선", "blue");
        doReturn(Optional.of(line)).when(lineRepository).findById(lineId);

        Station 개봉역 = new Station("개봉역");
        Station 구일역 = new Station("구일역");
        doReturn(개봉역).when(stationService).findById(upStationId);
        doReturn(구일역).when(stationService).findById(downStationId);

        // when
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, DEFAULT_DISTANCE);
        lineService.addSection(lineId, sectionRequest);

        // then
        assertThat(line.getSections()).hasSize(1);

    }
}
