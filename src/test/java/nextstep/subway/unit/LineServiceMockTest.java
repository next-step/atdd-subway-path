package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineInfo;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("호선의 대한 Mock 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @DisplayName("호선의 구간을 생성한다.")
    @Test
    void addSection() {

        final long 요청_호선 = 1L;
        final SectionRequest 요청_구간 = new SectionRequest(1L, 2L, 10);
        final Line line = new Line(new LineInfo("2호선", "green"));
        final Station upStation = new Station("강남역");
        final Station downStation = new Station("잠실역");

        when(stationService.findById(anyLong())).thenReturn(upStation)
                .thenReturn(downStation);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        lineService.addSection(요청_호선, 요청_구간);

        final InOrder inOrder = inOrder(stationService, lineRepository);
        inOrder.verify(stationService, times(2)).findById(anyLong());
        inOrder.verify(lineRepository, times(1)).findById(anyLong());

        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getSections().get(0).getLine().getLineInfo()).isEqualTo(new LineInfo("2호선", "green")),
                () -> assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo("잠실역"),
                () -> assertThat(line.getSections().get(0).getDistance()).isEqualTo(10)
        );
    }
}
