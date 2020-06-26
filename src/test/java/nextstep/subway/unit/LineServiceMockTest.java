package nextstep.subway.unit;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Long 역삼역_id = 2L;
        Long 선릉역_id = 3L;
        Long line_id = 1L;

        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = new Line("2호선", "green", 강남역, 역삼역, 10);

        when(stationService.findById(역삼역_id)).thenReturn(역삼역);
        when(stationService.findById(선릉역_id)).thenReturn(선릉역);
        when(lineRepository.findById(line_id)).thenReturn(Optional.of(line));

        final LineService lineService = new LineService(lineRepository, stationService);
        // when
        // lineService.addSection 호출
        lineService.addSection(line_id, new SectionRequest(역삼역_id, 선릉역_id, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findById(line_id);
        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
