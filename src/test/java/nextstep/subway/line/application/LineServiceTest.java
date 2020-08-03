package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    private LineService lineService;
    private List<Line> lines;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);

        Line line1 = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        ReflectionTestUtils.setField(line1, "id", 1L);
        Line line2 = new Line("3호선", "ORANGE", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);
        ReflectionTestUtils.setField(line2, "id", 2L);

        Station station1 = new Station("을지로4가");
        ReflectionTestUtils.setField(station1, "id", 1L);

        Station station2 = new Station("을지로3가");
        ReflectionTestUtils.setField(station2, "id", 2L);

        Station station3 = new Station("충무로역");
        ReflectionTestUtils.setField(station3, "id", 3L);

        Station station4 = new Station("동대문역");
        ReflectionTestUtils.setField(station4, "id", 4L);

        line1.addLineStation(new LineStation(1L, null, 5, 5));
        line1.addLineStation(new LineStation(2L, 1L, 5, 5));
        line1.addLineStation(new LineStation(4L, 2L, 5, 5));

        line2.addLineStation(new LineStation(1L, null, 5, 5));
        line2.addLineStation(new LineStation(3L, 1l, 5, 5));

        lines = Arrays.asList(line1, line2);
        stations = Arrays.asList(station1, station2, station3, station4);
    }

    @Test
    void findAllLinesAndStations() {
        // given
        when(lineRepository.findAll()).thenReturn(lines);
        when(stationRepository.findAllById(anyList())).thenReturn(stations);

        // when
        List<LineResponse> result = lineService.findAllLineAndStations();

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(0).getStations()).hasSize(3);
    }

}
