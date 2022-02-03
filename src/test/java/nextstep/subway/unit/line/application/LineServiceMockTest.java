package nextstep.subway.unit.line.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.application.dto.SectionRequest;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.repository.LineRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

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
        Line mockLine = new Line(1L, "초기 노선", "bg-red-500");
        Station mockUpStation = new Station("초기 상행");
        Station mockDownStation = new Station("초기 하행");

        when(lineRepository.findByIdWithStations(mockLine.getId())).thenReturn(Optional.of(mockLine));
        when(stationService.findById(mockUpStation.getId())).thenReturn(mockUpStation);
        when(stationService.findById(mockDownStation.getId())).thenReturn(mockDownStation);
        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(mockUpStation.getId())
            .downStationId(mockDownStation.getId())
            .distance(new Distance(100))
            .build();
        lineService.addSection(mockLine.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(mockLine.getSections().size()).isEqualTo(1);
    }
}
