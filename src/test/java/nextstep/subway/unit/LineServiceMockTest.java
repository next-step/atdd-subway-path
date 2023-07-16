package nextstep.subway.unit;

import nextstep.subway.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void 구간_추가() {
        // given
//        BDDMockito.given(stationService)
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅

        // when
//        lineService.addSection();
        // lineService.addSection 호출

        // then
        // lineService.findLineById 메서드를 통해 검증
    }
}
