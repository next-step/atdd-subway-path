package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;


    @Test
    void addSection(){
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        final StationResponse upStationResponse = new StationResponse(1L, "상행역", LocalDateTime.now(), LocalDateTime.now());
        when(stationService.findById(1L)).thenReturn(new Station("상행역"));

        final StationResponse downStationResponse = new StationResponse(2L, "하행역", LocalDateTime.now(), LocalDateTime.now());
        when(stationService.findById(2L)).thenReturn(new Station("상행역"));

        final StationResponse newStationResponse = new StationResponse(3L, "새로운역", LocalDateTime.now(), LocalDateTime.now());
        when(stationService.findById(3L)).thenReturn(new Station("상행역"));

        final Line line1 = new Line("1호선", "blue");
        when(lineRepository.save(line1)).thenReturn(new Line(1L, "1호선", "blue"));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line1));

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        final Line save = lineRepository.save(line1);
        final SectionRequest firstSectionRequest = new SectionRequest(upStationResponse.getId(), downStationResponse.getId(), 1);
        lineService.addSection(save.getId(), firstSectionRequest);

        final SectionRequest secondSectionRequest = new SectionRequest(downStationResponse.getId(), newStationResponse.getId(), 1);
        lineService.addSection(save.getId(), secondSectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findById(save.getId());
        assertThat(lineResponse.getStations().size()).isEqualTo(3);
    }
}
