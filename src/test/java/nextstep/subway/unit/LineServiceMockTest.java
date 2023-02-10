package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mock을 이용한 노선 통합 테스트")
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("노선을 생성한다.")
    void addSection() {
        Line line = new Line("신분당", "bg-900");
        Station upStation = Fixtures.판교역;
        Station downStation = Fixtures.정자역;

        when(stationService.findById(anyLong())).thenReturn(upStation)
            .thenReturn(downStation);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        lineService.addSection(1L, new SectionRequest(upStation.getId(), downStation.getId(), 10));

        verify(stationService, times((2))).findById(anyLong());
        verify(stationService, times(2)).findById(anyLong());
        verify(lineRepository, times(1)).findById(anyLong());

        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("판교역");
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("정자역");
        assertThat(line.getSections().get(0).getDistance()).isEqualTo(10);

    }
}
