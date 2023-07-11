package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 등록한다")
    void addSection() {
        // given
        Line line = new Line("line", "bg-red-600");
        long upStationId = 1L;
        long downStationId = 2L;
        long lineId = 1L;

        when(stationService.findById(upStationId)).thenReturn(new Station("지하철역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("또다른지하철역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(1L, new SectionRequest(upStationId, downStationId, 3));

        // then
        assertThat(lineService.findLineById(1L).getSections().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 제거한다")
    void deleteSection() {
        // given
        Line line = new Line("line", "bg-red-600");
        Station downStation = new Station("또다른지하철역");
        line.addSection(new Section(line, new Station("지하철역"), downStation, 10));

        long lineId = 1L;
        long downStationId = 2L;
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(stationService.findById(downStationId)).thenReturn(downStation);

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.deleteSection(lineId, downStationId);

        // then
        Assertions.assertThat(line.getSections().size()).isEqualTo(0);
    }
}
