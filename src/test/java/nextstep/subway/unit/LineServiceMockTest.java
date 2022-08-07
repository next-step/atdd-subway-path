package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private LineService lineService;

    @BeforeEach
    private void setup() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 구로역 = new Station("구로역");
        Station 신도림역 = new Station("신도림역");
        Station 영등포역 = new Station("영등포역");

        Line 일호선 = new Line("1호선", "blue");
        일호선.addSection(new Section(일호선, 구로역, 신도림역, 10));
        일호선.addSection(new Section(일호선, 신도림역, 영등포역, 15));

        when(stationService.findById(1L)).thenReturn(구로역);
        when(stationService.findById(2L)).thenReturn(신도림역);
        when(stationService.findById(3L)).thenReturn(영등포역);

        when(lineRepository.findById(1L)).thenReturn(Optional.of(일호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 15));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(1L);

        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getStations().stream().map(Station::getName)).contains("구로역", "신도림역", "영등포역");

    }
}
