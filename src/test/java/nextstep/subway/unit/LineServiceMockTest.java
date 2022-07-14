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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static nextstep.subway.unit.LineServiceMockTest.Stub.*;
import static org.assertj.core.api.Assertions.assertThat;
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
        when(stationService.findById(신대방역.getId())).thenReturn(신대방역);
        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(신대방역.getId(), 신림역.getId(), 8));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getStations()).contains(구로디지털단지역, 신대방역, 신림역);
    }

    protected static class Stub {
        public static final Station 구로디지털단지역 = new Station("구로디지털단지역");
        public static final Station 신대방역 = new Station("신대방역");
        public static final Station 신림역 = new Station("신림역");
        public static final Line 이호선 = new Line("이호선", "green", 구로디지털단지역, 신대방역, 10);

        static {
            ReflectionTestUtils.setField(구로디지털단지역, "id", 1L);
            ReflectionTestUtils.setField(신대방역, "id", 2L);
            ReflectionTestUtils.setField(신림역, "id", 3L);
            ReflectionTestUtils.setField(이호선, "id", 1L);
        }

    }
}
