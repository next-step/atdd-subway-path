package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("구간 삭제")
    @Test
    void deleteSection_success() {

        // given
        Line line = spy(Line.class);
        Station station = new Station();

        doNothing().when(line).removeStation(station);

        when(lineRepository.findById(any())).thenReturn(Optional.of(line));
        when(stationService.findById(any())).thenReturn(station);

        // when
        lineService.deleteSection(1L, 2L);

        // then
        // verify 메서드를 통해 검증
        verify(line).removeStation(station);
    }

    @DisplayName("구간 실패")
    @Test
    void deleteSection_fail() {

        // given
        Line line = spy(Line.class);
        Station station = new Station();

        when(lineRepository.findById(any())).thenReturn(Optional.of(line));
        when(stationService.findById(any())).thenReturn(station);
        doThrow(IllegalArgumentException.class).when(line).removeStation(any());

        // when & then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
