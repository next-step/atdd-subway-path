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

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineStationServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    private LineStationService lineStationService;

    private Line line;

    @Mock
    private Station station;

    private LineStation lineStation;

    private LineStationCreateRequest request;

    @BeforeEach
    void setUp() {
        lineStationService = new LineStationService(lineRepository, stationRepository);
        line = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
        lineStation = new LineStation(1L, null, 5, 5);
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
        given(lineRepository.findById(any())).willReturn(Optional.of(line));
        given(station.getId()).willReturn(1L);
        given(stationRepository.findAllById(anyList())).willReturn(Lists.newArrayList(station));
        request = new LineStationCreateRequest(1L, null, 5, 5);

        // when
        lineStationService.addLineStation(1L, request);

        // then
        assertAll(
                () -> assertThat(line.getLineStations()).isNotNull(),
                () -> assertThat(line.getStationInOrder()).hasSize(1)
        );
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        given(stationRepository.findAllById(anyList())).willReturn(Lists.newArrayList());
        request = new LineStationCreateRequest(1L, null, 5, 5);

        //when
        assertThatThrownBy(() -> lineStationService.addLineStation(1L, request))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
        line.addLineStation(lineStation);

        // when
        lineStationService.removeLineStation(1L, 1L);

        // then
        assertThat(line.getStationInOrder()).isEmpty();
    }
}
