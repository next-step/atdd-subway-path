package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
        Line line = new Line("5호선","purple");
        when(stationService.findById(1L)).thenReturn(new Station("마곡역"));
        when(stationService.findById(2L)).thenReturn(new Station("발산역"));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        lineService.addSection(1L,sectionRequest);


        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(1);
    }
}
