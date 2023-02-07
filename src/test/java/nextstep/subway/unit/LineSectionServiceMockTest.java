package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@DisplayName("노선 구간의 대한 Mock 테스트")
@ExtendWith(MockitoExtension.class)
class LineSectionServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @DisplayName("노선의 구간을 생성한다.")
    @Test
    void addSection() {

        final long 요청_호선 = 1L;
        final SectionRequest 요청_구간 = new SectionRequest(1L, 2L, 10);
        final Line line = new Line("2호선", "green");
        final Station 강남역 = 역_생성(1L, "강남역");
        final Station 잠실역 = 역_생성(2L, "잠실역");

        when(stationService.findById(anyLong())).thenReturn(강남역)
                .thenReturn(잠실역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        lineService.addSection(요청_호선, 요청_구간);

        final InOrder inOrder = inOrder(stationService, lineRepository);
        inOrder.verify(stationService, times(2)).findById(anyLong());
        inOrder.verify(lineRepository, times(1)).findById(anyLong());

        final List<Station> stations = line.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 잠실역)
        );
    }

    private Station 역_생성(final Long id, final String name) {
        final Station 역 = new Station(name);
        ReflectionTestUtils.setField(역, "id", id);
        return 역;
    }
}
