package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);
    }

    @Test
    @DisplayName("현재 존재하는 모든 노선과 포함된 역정보를 제공한다.")
    void findAllLinesForMaps() {
        // given
        Station station1 = new Station("강남역");
        ReflectionTestUtils.setField(station1, "id", 1L);
        Station station2 = new Station("역삼역");
        ReflectionTestUtils.setField(station2, "id", 2L);
        Station station3 = new Station("양재역");
        ReflectionTestUtils.setField(station3, "id", 3L);

        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station1, station2, station3));

        Line line1 = new Line("신분당선", "RED", LocalTime.now(), LocalTime.now(), 10);
        ReflectionTestUtils.setField(line1, "id", 1L);
        Line line2 = new Line("2호선", "GREEN", LocalTime.now(), LocalTime.now(), 10);
        ReflectionTestUtils.setField(line2, "id", 2L);

        when(lineRepository.findAll()).thenReturn(Lists.newArrayList(line1, line2));

        line1.addLineStation(new LineStation(1L, null, 10, 10));
        line1.addLineStation(new LineStation(2L, 1L, 10, 10));
        line1.addLineStation(new LineStation(3L, 2L, 10, 10));
        line2.addLineStation(new LineStation(1L, null, 10, 10));
        line2.addLineStation(new LineStation(3L, 1L, 10, 10));

        // when
        List<LineResponse> result = lineService.findAllLinesForMaps();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStations()).hasSize(3);
        assertThat(result.get(1).getStations()).hasSize(2);
    }    
}
