package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class MapServiceTest {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    Line line;
    Station station;

    @BeforeEach
    void setUp() {
        this.lineRepository = mock(LineRepository.class);
        this.stationRepository = mock(StationRepository.class);

        line = new Line("1호선", "blue", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
        station = new Station("용산역");
        ReflectionTestUtils.setField(station, "id", 1L);
    }

    @Test
    void findAllMaps() {
        // given
        MapService mapService = new MapService(lineRepository, stationRepository);
        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(line));
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station));

        // when
        MapResponse mapResponse = mapService.findAllMaps();

        // then
        assertThat(mapResponse).isNotNull();

        verify(lineRepository).findAll();
        verify(stationRepository).findAllById(anyList());
    }
}
