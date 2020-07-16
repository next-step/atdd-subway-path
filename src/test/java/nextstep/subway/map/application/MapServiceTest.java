package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private Station station;

    private List<Line> lines;

    private MapService mapService;

    @BeforeEach
    void setUp() {
        mapService = new MapService(lineRepository, stationRepository);

        Line line = new Line("2호선", "GREEN", LocalTime.of(5,30), LocalTime.of(23, 30), 10);
        LineStation lineStation = new LineStation(1L, null, 5, 5);
        line.addLineStation(lineStation);

        lines = Lists.newArrayList(line);
    }

    @DisplayName("지하철 노선과 역을 조회한다.")
    @Test
    void findLineStation() {
        when(lineRepository.findAll()).thenReturn(lines);
        when(station.getId()).thenReturn(1L);
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station));

        MapResponse mapResponse = mapService.findAllLineAndStation();

        // then
        assertAll(
                () -> assertThat(mapResponse).isNotNull(),
                () -> assertThat(mapResponse.getLineResponses()).hasSize(1)
        );

    }
}
