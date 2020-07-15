package nextstep.subway.line.application;

import com.google.common.collect.Lists;
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

    @BeforeEach
    void setUp() {
        this.lineService = new LineService(lineRepository, stationRepository);
    }

    @Test
    void findAllLinesWithStations() {
        // given
        Line line = new Line();
        Station station1 = new Station();
        Station station2 = new Station();
        ReflectionTestUtils.setField(station1, "id", 1L);
        ReflectionTestUtils.setField(station2, "id", 2L);
        line.addLineStation(new LineStation(1L, null, 10, 10));
        line.addLineStation(new LineStation(2L, 1L, 10, 10));

        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(line, new Line()));
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station1, station2, new Station()));

        // when
        List<LineResponse> lineResponses = lineService.findAllLinesWithStations();

        // then
        assertThat(lineResponses.size()).isEqualTo(2);
        assertThat(lineResponses.get(0).getStations().size()).isEqualTo(2);
    }
}
