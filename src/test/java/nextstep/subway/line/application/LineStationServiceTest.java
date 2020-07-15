package nextstep.subway.line.application;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationCreateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineStationServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private LineStationService lineStationService;

    Line line;
    @BeforeEach
    void setUp() {
        lineStationService = new LineStationService(lineRepository, stationRepository);
        line = new Line("1호선", "blue", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
        // given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        Station station = new Station("용산역");
        ReflectionTestUtils.setField(station, "id", 1L);
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station));

        LineStationCreateRequest request = new LineStationCreateRequest(station.getId(), null, 4, 4);

        // when
        lineStationService.addLineStation(anyLong(), request);

        // that
        assertThat(line.getLineStations()).isNotNull();
        assertThat(line.getStationInOrder()).hasSize(1);
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        // given
        LineStationCreateRequest request = new LineStationCreateRequest(1L, null, 4, 4);

        // when
        assertThatThrownBy(() -> lineStationService.addLineStation(2L, request))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
        // given
        line.addLineStation(new LineStation(2L, null, 4, 4));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        // when
        lineStationService.removeLineStation(anyLong(),2L);

        // then
        assertThat(line.getStationInOrder()).isEmpty();
    }
}
