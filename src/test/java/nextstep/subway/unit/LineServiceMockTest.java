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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    public static String 강남역_이름 = "강남역";
    public static String 분당역_이름 = "분당역";
    public static String 신분당선_이름 = "신분당선";
    public static String 신분당선_색 = "Green";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        when(stationService.findById(any())).thenReturn(new Station(강남역_이름));
        when(stationService.findById(any())).thenReturn(new Station(분당역_이름));
        when(lineRepository.findById(any())).thenReturn(Optional.of(new Line(신분당선_이름,신분당선_색)));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L,2L,10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(1L);
        assertThat(line.getName()).isEqualTo(신분당선_이름);
        assertThat(line.getColor()).isEqualTo(신분당선_색);
    }
}
