package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService sut;

    private Station 광교역;
    private Station 광교중앙역;

    @BeforeEach
    void setUp() {
        sut = new LineService(lineRepository, stationService);

        광교역 = createTestStation(1L, "광교역");
        광교중앙역 = createTestStation(2L, "광교중앙역");
    }

    @DisplayName("노선 추가")
    @Test
    void createLine() {
        // given
        Map<Long, Line> lineStorage = new HashMap<>();
        prepareStations(광교역, 광교중앙역);
        when(lineRepository.findById(any(Long.class)))
                .thenAnswer(args -> {
                    var id = (Long) args.getArgument(0);
                    return Optional.ofNullable(lineStorage.getOrDefault(id, null));
                });
        when(lineRepository.save(any(Line.class)))
                .thenAnswer(args -> {
                    var line = (Line) args.getArgument(0);
                    line.setId((long) (lineStorage.size() + 1));
                    lineStorage.put(line.getId(), line);
                    return line;
                });
        when(stationService.createStationResponse(any(Station.class))).thenCallRealMethod();

        var distance = 10;
        var lineRequest = new LineRequest("신분당선", "red", 광교역.getId(), 광교중앙역.getId(), distance);

        // when
        var lineResponse = sut.saveLine(lineRequest);

        // then
        var expectedResponse = new LineResponse(
                lineResponse.getId(),
                lineRequest.getName(),
                lineRequest.getColor(),
                List.of(
                        new StationResponse(광교역.getId(), 광교역.getName()),
                        new StationResponse(광교중앙역.getId(), 광교중앙역.getName())
                )
        );
        var line = lineRepository.findById(lineResponse.getId());

        assertAll(
                () -> assertThat(lineResponse).isEqualTo(expectedResponse),
                () -> assertThat(line).isNotEmpty(),
                () -> assertThat(line.get().getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(line.get().getColor()).isEqualTo(lineRequest.getColor()),
                () -> assertThat(line.get().getStations()).containsExactly(광교역, 광교중앙역)
        );
    }

    @DisplayName("모든 노선 조회")
    @Test
    void showLines() {
        // given
        var line = createLine(1L, "신분당선", "red", 광교역, 광교중앙역, 10);
        when(lineRepository.findAll()).thenReturn(List.of(line));
        when(stationService.createStationResponse(any(Station.class))).thenCallRealMethod();

        // when
        var lineResponses = sut.showLines();

        // then
        var expectedResponses = List.of(
                new LineResponse(
                        line.getId(),
                        line.getName(),
                        line.getColor(),
                        List.of(
                                new StationResponse(광교역.getId(), 광교역.getName()),
                                new StationResponse(광교중앙역.getId(), 광교중앙역.getName())
                        )
                )
        );
        assertThat(lineResponses).isEqualTo(expectedResponses);
    }

    @DisplayName("ID로 노선 조회")
    @Test
    void findLineById() {
        // given
        var line = createLine(1L, "신분당선", "red", 광교역, 광교중앙역, 10);
        when(lineRepository.findById(line.getId()))
                .thenReturn(Optional.of(line));
        when(stationService.createStationResponse(any(Station.class))).thenCallRealMethod();

        // when
        var lineResponse = sut.findById(line.getId());

        // then
        var expectedResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                List.of(
                        new StationResponse(광교역.getId(), 광교역.getName()),
                        new StationResponse(광교중앙역.getId(), 광교중앙역.getName())
                )
        );
        assertThat(lineResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("노선 업데이트")
    @Test
    void updateLine() {
        // given
        var line = createLine(1L, "신분당선", "red", 광교역, 광교중앙역, 10);
        when(lineRepository.findById(line.getId()))
                .thenReturn(Optional.of(line));

        // when
        var updateRequest = new LineRequest("구분당선", "black", 광교역.getId(), 광교중앙역.getId(), 10);
        sut.updateLine(line.getId(), updateRequest);

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(updateRequest.getName()),
                () -> assertThat(line.getColor()).isEqualTo(updateRequest.getColor())
        );
    }

    @DisplayName("ID로 노선 삭제")
    @Test
    void deleteLine() {
        // when
        var lineId = 1L;
        sut.deleteLine(lineId);

        // then
        verify(lineRepository).deleteById(lineId);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        var distance = 10;
        var line = createLine(1L, "신분당선", "red", 광교역, 광교중앙역, distance);
        var 상현역 = createTestStation(3L, "상현역");
        prepareStations(광교중앙역, 상현역);
        when(lineRepository.findById(line.getId()))
                .thenReturn(Optional.of(line));

        // when
        sut.addSection(line.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), distance));

        // then
        assertThat(line.getStations()).containsExactly(광교역, 광교중앙역, 상현역);
    }

    @DisplayName("역 ID 로 구간 삭제")
    @Test
    void deleteLineById() {
        // given
        var line = createLine(1L, "신분당선", "red", 광교역, 광교중앙역, 10);
        var 상현역 = createTestStation(3L, "상현역");
        prepareStations(상현역);

        line.addSection(광교중앙역, 상현역, 5);
        when(lineRepository.findById(line.getId()))
                .thenReturn(Optional.of(line));
        // when
        sut.deleteSection(line.getId(), 상현역.getId());

        // then
        assertThat(line.getStations()).containsExactly(광교역, 광교중앙역);
    }

    private Line createLine(Long id, String name, String color, Station upStation, Station downStation, Integer distance) {
        var line = new Line(name, color);
        line.setId(id);
        line.addSection(upStation, downStation, distance);
        return line;
    }

    private Station createTestStation(Long id, String name) {
        var station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }

    private void prepareStations(Station ...station) {
        for (var s : station) {
            when(stationService.findById(s.getId())).thenReturn(s);
        }
    }
}
