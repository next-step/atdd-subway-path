package nextstep.subway.unit;

import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        // when
        // lineService.addSection 호출

        // then
        // line.findLineById 메서드를 통해 검증
    }
}
