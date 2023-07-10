package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    /**
     * Given 노선이 있을 때
     * When 구간을 추가하면
     * Then 구간을 조회 할 수 있다.
     */
    @Test
    void addSection() {
        // given
        final long upStationId = 1L;
        final long downStationId = 2L;
        final long lineId = 1L;
        final int distance = 10;

        when(stationService.findById(upStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("역삼역"));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line("2호선", "bg-green-600")));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        when(stationService.findById(upStationId)).thenReturn(new Station("강남역"));
        when(stationService.findById(downStationId)).thenReturn(new Station("역삼역"));

        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);
        lineService.addSection(lineId, request);

        // then
        assertThat(lineService.findByLineId(lineId).getSections().size()).isEqualTo(1);
    }
}
