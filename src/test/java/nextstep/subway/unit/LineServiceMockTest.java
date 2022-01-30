package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DisplayName("구간 서비스 목 테스트")
@SuppressWarnings("ALL")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 양재역 = new Station("양재역");

        Line 신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 10);

        when(lineRepository.findById(anyLong())).thenReturn(ofNullable(신분당선));
        when(stationService.findById(1L)).thenReturn(판교역);
        when(stationService.findById(2L)).thenReturn(양재역);

        // when
        // lineService.addSection 호출
        lineService.addSection(
                1L,
                new SectionRequest(1L, 2L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(신분당선.getSectionSize()).isEqualTo(2);
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역, 양재역);
    }
}
