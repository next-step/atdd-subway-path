package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    /**
     * 신논현역                          정자역 --- *분당선* (25) --- 이매역
     * |                                 |
     * *신분당선* (10)                  *분당선* (20)
     * |                                 |
     * 강남역  --- *신분당선* (15) ---  판교역
     */

    @BeforeEach
    void setUp() {
        Line 신분당선 = Line.of("신분당선", "bg-yellow-600");
        Line 분당선 = Line.of("분당선", "bg-red-600");

        when(stationRepository.findById(1L)).thenReturn(Optional.of(신논현역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(정자역));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(판교역));
        when(stationRepository.findById(5L)).thenReturn(Optional.of(이매역));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(신분당선));
        when(lineRepository.findById(2L)).thenReturn(Optional.of(분당선));
        when(lineRepository.findAll()).thenReturn(List.of(신분당선, 분당선));

        LineService lineService = new LineService(lineRepository, stationRepository);

        lineService.addSection(1L, SectionRequest.of(1L, 2L, 10));
        lineService.addSection(1L, SectionRequest.of(2L, 4L, 15));
        lineService.addSection(2L, SectionRequest.of(4L, 3L, 20));
        lineService.addSection(2L, SectionRequest.of(3L, 5L, 25));
    }

    @DisplayName("출발지와 목적지를 통해 경로를 조회한다.")
    @Test
    void findPathSourceToTarget() {
        // when
        // pathService.findPath 를 사용하여 출발역과 도착역을 조회하면
        PathService pathService = new PathService(lineRepository, stationRepository);

        // then
        // pathResponse 에 경로와 거리가 존재한다.
        PathResponse pathResponse = pathService.findPath(PathRequest.of(1L, 5L));
        assertAll(
                () -> assertThat(pathResponse.getDistance()).isEqualTo(70),
                () -> assertThat(pathResponse.getStations()).extracting("name").containsExactly("신논현역", "강남역", "판교역", "정자역", "이매역")
        );
    }

    @DisplayName("존재하지 않는 역을 출발역으로 지정할 경우")
    @Test
    void findPathNotExistsSourceStation() {
        // given 그래프에 등록되지 않은 역을 생성하고
        when(stationRepository.findById(6L)).thenReturn(Optional.of(new Station("없는역")));

        // when
        // pathService.findPath 를 사용하여 그래프에 등록되지 않은 역을 출발역으로 설정한후 조회하면 에러를 발생한다.
        PathService pathService = new PathService(lineRepository, stationRepository);

        assertThatThrownBy(() -> pathService.findPath(PathRequest.of(6L, 5L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("graph must contain the source vertex");
    }
}
