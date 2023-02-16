package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
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
@DisplayName("구간 서비스 단위 테스트 with Mock")
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    @DisplayName("지하철 2호선에 구간을 추가할 수 있다")
    void addSection() {
        // given
        Long givenUpStationId = 1L;
        Long givenDownStationId = 2L;

        String givenUpStationName = "강남역";
        String givenDownStationName = "선릉역";

        Station givenUpStation = new Station(givenUpStationName);
        Station givenDownStation = new Station(givenDownStationName);
        int givenDistance = 20;

        Long givenLineId = 1L;
        Line givenLine = new Line("2호선", "bg-green-35");

        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(givenUpStationId)).thenReturn(givenUpStation);
        when(stationService.findById(givenDownStationId)).thenReturn(givenDownStation);
        when(lineRepository.findById(givenLineId)).thenReturn(Optional.of(givenLine));
        when(stationService.createStationResponse(givenUpStation)).thenReturn(new StationResponse(givenUpStationId, givenUpStationName));
        when(stationService.createStationResponse(givenDownStation)).thenReturn(new StationResponse(givenDownStationId, givenDownStationName));

        // when
        // lineService.addSection 호출
        lineService.addSection(givenLineId, new SectionRequest(givenUpStationId, givenDownStationId, givenDistance));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(givenLineId);
        assertThat(lineResponse)
            .isNotNull()
            .hasFieldOrPropertyWithValue("name", "2호선")
            .hasFieldOrPropertyWithValue("color", "bg-green-35");

        assertThat(lineResponse.getStations())
            .extracting(StationResponse::getName)
            .containsExactly(givenUpStationName, givenDownStationName);
    }
}
