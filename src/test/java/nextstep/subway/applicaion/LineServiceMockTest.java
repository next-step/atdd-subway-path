package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("지하철 구간 관련 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        this.lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // given
        Line 이호선 = Stub.이호선_생성.get();
        Station 구로디지털단지역 = Stub.구로디지털단지역_생성.get();
        Station 신대방역 = Stub.신대방역_생성.get();
        Station 신림역 = Stub.신림역_생성.get();

        when(stationService.findById(신대방역.getId())).thenReturn(신대방역);
        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        // when
        lineService.addSection(이호선.getId(), new SectionRequest(신대방역.getId(), 신림역.getId(), 8));

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getStations()).contains(구로디지털단지역, 신대방역, 신림역);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteSection() {
        // given
        Line 이호선 = Stub.이호선_생성.get();
        Station 구로디지털단지역 = Stub.구로디지털단지역_생성.get();
        Station 신대방역 = Stub.신대방역_생성.get();
        Station 신림역 = Stub.신림역_생성.get();

        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        이호선.addSection(신대방역, 신림역, 8);

        // when
        lineService.deleteSection(이호선.getId(), 이호선.getLastDownStation().getId());

        // then
        assertThat(이호선.getStations()).contains(구로디지털단지역, 신대방역);
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void saveLine() {
        // given
        Line 이호선 = Stub.이호선_생성.get();
        Station 신대방역 = Stub.신대방역_생성.get();
        Station 신림역 = Stub.신림역_생성.get();

        when(stationService.findById(신대방역.getId())).thenReturn(신대방역);
        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.save(any())).thenReturn(이호선);

        // when
        LineResponse response = lineService.saveLine(new LineRequest("2호선", "green", 신대방역.getId(), 신림역.getId(), 10));

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("2호선");
        assertThat(response.getColor()).isEqualTo("green");
        assertThat(response.getStations()).hasSize(3);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Line 이호선 = Stub.이호선_생성.get();

        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        // when
        lineService.updateLine(이호선.getId(), new LineRequest("신분당선", "red"));

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getName()).isEqualTo("신분당선");
        assertThat(line.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        Line 이호선 = Stub.이호선_생성.get();

        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.empty());

        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> lineService.findLineById(이호선.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철역에 해당하는 노선 조회")
    @Test
    void findByStations() {
        // given
        Station 구로디지털단지역 = Stub.구로디지털단지역_생성.get();
        Station 신대방역 = Stub.신대방역_생성.get();
        Line 이호선 = Stub.이호선_생성.get();

        when(lineRepository.findAll()).thenReturn(List.of(이호선));

        // when
        Line line = lineService.findByStations(List.of(구로디지털단지역, 신대방역));

        // then
        assertThat(line).isEqualTo(이호선);
    }

    @DisplayName("노선에 없는 지하철역으로 노선을 조회하는 경우 예외 발생")
    @Test
    void findByStationsUnknownStationInLines() {
        // given
        Station 구로디지털단지역 = Stub.구로디지털단지역_생성.get();
        Station 신림역 = Stub.신림역_생성.get();
        Line 이호선 = Stub.이호선_생성.get();

        when(lineRepository.findAll()).thenReturn(List.of(이호선));

        // then
        assertThatThrownBy(() -> lineService.findByStations(List.of(구로디지털단지역, 신림역)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static class Stub {
        public static final Supplier<Station> 구로디지털단지역_생성 = () -> createStation(1L, "구로디지털단지역");
        public static final Supplier<Station> 신대방역_생성 = () -> createStation(2L, "신대방역");
        public static final Supplier<Station> 신림역_생성 = () -> createStation(3L, "신림역");
        public static final Supplier<Line> 이호선_생성 = () -> {
            Line line = new Line("2호선", "green", 구로디지털단지역_생성.get(), 신대방역_생성.get(), 10);
            ReflectionTestUtils.setField(line, "id", 1L);
            return line;
        };

        public static Station createStation(Long id, String name) {
            Station station = new Station(name);
            ReflectionTestUtils.setField(station, "id", id);
            return station;
        }

    }
}
