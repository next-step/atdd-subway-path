package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineSections;
import nextstep.subway.line.LineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import nextstep.subway.StationService;
import nextstep.subway.line.LineRepository;

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
        Line line = new Line(null, "신분당선", "RED",  new LineSections());
        // when
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }
}
