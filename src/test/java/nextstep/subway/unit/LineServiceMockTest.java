package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 첫번째역 = new Station(1L,"첫번째역");
        Station 두번째역 = new Station(2L,"두번째역");
        Line 첫번째노선 = new Line("첫번째노선", "BLUE");

        when(lineRepository.findById(첫번째노선.getId())).thenReturn(Optional.of(첫번째노선));
        when(stationRepository.findById(첫번째역.getId())).thenReturn(Optional.of(첫번째역));
        when(stationRepository.findById(두번째역.getId())).thenReturn(Optional.of(두번째역));

        // when
        // lineService.addSection 호출
        lineService.addSection(첫번째노선.getId(),
            SectionRequest.builder().upStationId(첫번째역.getId()).downStationId(두번째역.getId())
                .distance(10L).build());
        // then
        // lineService.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findLineById(첫번째노선.getId());
        assertThat(response.getColor()).isEqualTo("BLUE");
        assertThat(response.getStations().stream().map(station -> station.getName()).collect(
            Collectors.toList())).containsExactly("첫번째역", "두번째역");
    }
}
