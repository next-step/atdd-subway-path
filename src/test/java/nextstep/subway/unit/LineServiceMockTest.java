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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Long lineId = 10L;
        SectionRequest request = setUpStubs(lineId);

        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(lineId, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findById(lineId);
        List<Long> stationIds = response.getStations().stream().map(StationResponse::getId).collect(Collectors.toList());
        assertThat(stationIds).contains(request.getDownStationId(), request.getUpStationId());
    }

    private SectionRequest setUpStubs(Long lineId) {
        Line line = new Line("신분당선", "red");
        Station upStation = new Station("양재역");
        Station downStation = new Station("판교역");
        SectionRequest request = new SectionRequest(1L, 2L, 10);

        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(stationService.findById(request.getUpStationId())).thenReturn(upStation);
        when(stationService.findById(request.getDownStationId())).thenReturn(downStation);
        when(stationService.createStationResponse(upStation)).thenReturn(new StationResponse(request.getUpStationId(), upStation.getName()));
        when(stationService.createStationResponse(downStation)).thenReturn(new StationResponse(request.getDownStationId(), downStation.getName()));

        return request;
    }

}
