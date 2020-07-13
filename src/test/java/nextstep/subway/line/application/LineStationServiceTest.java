package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @BeforeEach
    void setUp() {
        lineStationService = new LineStationService(lineRepository, stationRepository);
        line = new Line("2호선", "GREEN", LocalTime.of(5, 30), LocalTime.of(23, 30), 5);
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {

        // stubbing
        final long stationId = 1L;
        given(station.getId()).willReturn(stationId);
        given(stationRepository.findAllById(anyList())).willReturn(List.of(station));
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        final LineStationCreateRequest request = new LineStationCreateRequest(stationId, null, 5, 5);
        lineStationService.addLineStation(2L, request);

        // then
        assertThat(line.getLineStations()).isNotNull();
        assertThat(line.getStationInOrder()).hasSize(1);
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {

        // when
        final long stationId = 1L;
        final LineStationCreateRequest request = new LineStationCreateRequest(stationId, null, 5, 5);

        // then
        assertThatThrownBy(() -> lineStationService.addLineStation(2L, request));
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
    }
}
