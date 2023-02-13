package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선에 새로운 구간 추가하기")
    @Test
    void addSection() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("1호선", "남색")));
        when(stationService.findById(1L)).thenReturn(new Station("서울역"));
        when(stationService.findById(2L)).thenReturn(new Station("시청역"));

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        Line line = lineService.findLineById(1L);

        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getColor()).isEqualTo("남색");
    }
}
