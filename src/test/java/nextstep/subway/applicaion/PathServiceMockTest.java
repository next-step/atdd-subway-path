package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("지하철 경로 관련 with Mock")
@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    private PathService pathService;

    @BeforeEach
    void setUp() {
        this.pathService = new PathService(stationService, lineService);
    }

    @DisplayName("두 지하철역 경로 찾기")
    @Test
    void findPath() {
        // given
        Station 서울역 = Stub.서울역_생성.get();
        Station 종각역 = Stub.종각역_생성.get();
        Line 일호선 = Stub.일호선_생성.get();

        when(stationService.findById(서울역.getId())).thenReturn(서울역);
        when(stationService.findById(종각역.getId())).thenReturn(종각역);
        when(lineService.findByStations(List.of(서울역, 종각역))).thenReturn(일호선);

        // when
        PathResponse response = pathService.findPath(서울역.getId(), 종각역.getId());

        // then
        assertThat(response.getStations()).hasSize(3);
        assertThat(response.getDistance()).isEqualTo(13);
    }

    private static class Stub {
        public static final Supplier<Station> 서울역_생성 = () -> createStation(1L, "서울역");

        public static final Supplier<Station> 시청역_생성 = () -> createStation(2L, "시청역");

        public static final Supplier<Station> 종각역_생성 = () -> createStation(3L, "종각역");

        public static final Supplier<Line> 일호선_생성 = () -> {
            Line 일호선 = new Line("일호선", "blue");
            일호선.addSection(서울역_생성.get(), 시청역_생성.get(), 5);
            일호선.addSection(시청역_생성.get(), 종각역_생성.get(), 8);
            ReflectionTestUtils.setField(일호선, "id", 3L);
            return 일호선;
        };

        private static Station createStation(Long id, String name) {
            Station station = new Station(name);
            ReflectionTestUtils.setField(station, "id", id);
            return station;
        }
    }
}
