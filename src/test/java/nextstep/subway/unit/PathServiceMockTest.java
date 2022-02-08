package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("최단 경로 관리 - Mock")
@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    private static final int DEFAULT_DISTANCE = 5;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private PathService pathService;

    private List<Line> lines = new ArrayList<>();
    private Line 일호선;
    private Line 이호선;

    private Station 부평역;
    private Station 신도림역;
    private Station 강남역;
    private Station 역삼역;

    @BeforeEach
    void setUp() {
        부평역 = createStationEntity(1L, "부평역");
        신도림역 = createStationEntity(2L, "신도림역");
        강남역 = createStationEntity(3L, "강남역");
        역삼역 = createStationEntity(4L, "역삼역");

        일호선 = createLineEntity("일호선", "red");
        일호선.addSection(부평역, 신도림역, DEFAULT_DISTANCE);

        이호선 = createLineEntity("이호선", "green");
        이호선.addSection(신도림역, 강남역, DEFAULT_DISTANCE);

        lines.add(일호선);
        lines.add(이호선);
    }

    @DisplayName("최단 경로를 조회")
    @Test
    void shortPath() {
        // given
        int sumDistance = DEFAULT_DISTANCE * 2;

        when(stationService.findById(any())).thenReturn(부평역)
                                            .thenReturn(강남역);
        when(lineRepository.findAll()).thenReturn(lines);

        // when
        PathResponse pathResponse = pathService.shortPath(부평역.getId(), 강남역.getId());

        // then
        assertAll(
                () -> assertThat(pathResponse.getStations()).hasSize(3),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(sumDistance)
        );
    }

    @DisplayName("동일한 출발지와 도착역으로 최단 경로 조회하면 예외 발생")
    @Test
    void sourceEqualsTargetException() {
        // when, then
        assertThatThrownBy(() -> pathService.shortPath(부평역.getId(), 부평역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("연결이 안된 구간으로 요청하면 예외 발생")
    @Test
    void notConnectException() {
        // given
        when(stationService.findById(any())).thenReturn(부평역)
                                            .thenReturn(역삼역);
        when(lineRepository.findAll()).thenReturn(lines);

        // when

        assertThatThrownBy(() -> pathService.shortPath(부평역.getId(), 역삼역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Station createStationEntity(Long id, String name) {
        return new Station(id, name);
    }

    private Line createLineEntity(String name, String color) {
        return new Line(name, color);
    }
}
