package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("name", "color")));
        when(stationService.findById(1L)).thenReturn(new Station("up"));
        when(stationService.findById(2L)).thenReturn(new Station("down"));
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 20);
        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(1L, sectionRequest);
        // then
        // lineService.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findById(1L);
        assertThat(response.getName()).isEqualTo("name");
        assertThat(response.getColor()).isEqualTo("color");
    }
}
