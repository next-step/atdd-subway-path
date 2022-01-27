package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    final Long upStationId = 1L;
    final Long downStationId = 2L;
    final Long lineId = 1L;
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line line = new Line("line", "color");
        Station upStation = new Station(upStationId, "upStation");
        Station downStation = new Station(downStationId, "downStation");
        when(stationService.findById(upStationId)).thenReturn(upStation);
        when(stationService.findById(downStationId)).thenReturn(downStation);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(lineId, SectionRequest.of(upStationId, downStationId, 99));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(lineId);
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }
}
