package subway.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.repository.LineRepository;
import subway.service.LineService;
import subway.service.StationService;
import subway.utils.LineAssertions;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        Station 강남역 = Station.builder()
            .id(1L)
            .name("강남역")
            .build();

        Station 논현역 = Station.builder()
            .id(2L)
            .name("논현역")
            .build();

        Station 광교역 = Station.builder()
            .id(3L)
            .name("광교역")
            .build();

        Line 신분당선 = Line.builder()
            .name("신분당선")
            .color("bg-red-600")
            .distance(30L)
            .upStation(강남역)
            .downStation(논현역)
            .build();

        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(any())).thenReturn(Optional.of(신분당선));
        when(stationService.findStationById(2L)).thenReturn(논현역); // 기존 구간의 상행종점역
        when(stationService.findStationById(3L)).thenReturn(광교역); // 새로운 구간의 하행종점역
        SectionRequest request = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(10L)
            .build();

        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);
        Line 구간저장후_노선 = lineService.addSection(1L, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        when(lineRepository.findById(any())).thenReturn(Optional.of(구간저장후_노선));
        Line 조회한_노선 = lineService.findLineById(any());

        LineAssertions.구간추가후_검증(조회한_노선);
    }
}
