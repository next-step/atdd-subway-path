package nextstep.subway.unit;

import static nextstep.subway.utils.MockString.line2;
import static nextstep.subway.utils.MockString.봉천역;
import static nextstep.subway.utils.MockString.서울대입구역;
import static nextstep.subway.utils.MockString.초록색;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line mockLine = new Line(line2, 초록색);
        Station upStation = new Station(서울대입구역);
        Station downStation = new Station(봉천역);
        SectionRequest request = mock(SectionRequest.class);

        when(request.getUpStationId()).thenReturn(1L);
        when(request.getDownStationId()).thenReturn(2L);
        when(request.getDistance()).thenReturn(10);
        when(lineRepository.findById(any())).thenReturn(Optional.of(mockLine));
        when(stationService.findById(1L)).thenReturn(upStation);
        when(stationService.findById(2L)).thenReturn(downStation);

        // when
        // lineService.addSection 호출
        lineService.addSection(mockLine.getId(), request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line updated = lineService.findLineById(mockLine.getId());
        assertThat(updated.getSections().get(0).getUpStation()).isEqualTo(upStation);
        assertThat(updated.getSections().get(0).getDownStation()).isEqualTo(downStation);
    }
}
