package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("지하철 구간 생성")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(stationService.findById(1L)).willReturn(new Station("강남역"));
        given(stationService.findById(2L)).willReturn(new Station("역삼역"));
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("신분당선", "yellow")));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        List<StationResponse> stations = lineService.findById(1L).getStations();
        assertThat(stations).hasSize(2);
    }
}
