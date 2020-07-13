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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineStationServiceTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    private LineStationService lineStationService;

    @Mock
    private Line line;

    @Mock
    private Station station;

    @Mock
    private Station preStation;

    @BeforeEach
    void setUp() {
        lineStationService = new LineStationService(lineRepository, stationRepository);
        station = new Station("강남역");
        preStation = new Station("교대역");
    }

    @DisplayName("지하철 노선에 역을 등록한다.")
    @Test
    void addLineStation1() {
    }

    @DisplayName("존재하지 않는 역을 등록한다.")
    @Test
    void addLineStation2() {
        // given
        given(lineRepository.findById(any())).willReturn(null);

        // when
        assertThatThrownBy(() -> lineStationService.removeLineStation(1L, 1L))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 역을 제외한다.")
    @Test
    void removeLineStation() {
        // given
        given(lineRepository.findById(any())).willReturn(Optional.of(line));

        // when
        lineStationService.removeLineStation(1L, 1L);

        // then
        verify(line).removeLineStationById(1L);
    }
}
