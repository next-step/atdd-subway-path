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

    @BeforeEach
    void setUp() {
        lineStationService = new LineStationService(lineRepository, stationRepository);
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
        // given
        Station station = new Station();
        ReflectionTestUtils.setField(station, "id", 1L);
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList(station));

        Line line = new Line();
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        // when
        LineStationCreateRequest request = new LineStationCreateRequest(1L, null, 10, 10);
        lineStationService.addLineStation(1L, request);

        // then
        assertThat(line.getStationInOrder()).hasSize(1);
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        //given
        when(stationRepository.findAllById(anyList())).thenReturn(Lists.newArrayList());        

        //when
        LineStationCreateRequest request = new LineStationCreateRequest(1L, null, 10, 10);
        assertThatThrownBy(() -> lineStationService.addLineStation(1L, request))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
        //given
        Line line = new Line();
        ReflectionTestUtils.setField(line, "id", 1L);
        line.addLineStation(new LineStation(1L, null, 10, 10));
        line.addLineStation(new LineStation(2L, 1L, 10, 10));
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));

        //when
        lineStationService.removeLineStation(1L, 1L);

        //then
        assertThat(line.getStationInOrder()).hasSize(1);
    }
}
