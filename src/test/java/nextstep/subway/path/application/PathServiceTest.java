package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.ui.FindType;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@DisplayName("경로 탐색 서비스 유닛 테스트")
class PathServiceTest {

    @Mock
    @Deprecated
    private LineRepository lineRepository;

    @Mock
    @Deprecated
    private StationRepository stationRepository;

    private List<LineResponse> lineResponses;

    @Mock
    private LineService lineService;

    private PathService pathService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        pathService = new PathService(lineRepository, stationRepository);

        final StationResponse 강남역 = new StationResponse(1L, "강남역", LocalDateTime.now(), LocalDateTime.now());
        final StationResponse 역삼역 = new StationResponse(2L, "역삼역", LocalDateTime.now(), LocalDateTime.now());
        final StationResponse 선릉역 = new StationResponse(3L, "선릉역", LocalDateTime.now(), LocalDateTime.now());
        final StationResponse 양재역 = new StationResponse(4L, "양재역", LocalDateTime.now(), LocalDateTime.now());
        final StationResponse 양시숲 = new StationResponse(5L, "양재시민의숲", LocalDateTime.now(), LocalDateTime.now());

        // 2호선
        final LineStationResponse lsr1 = new LineStationResponse(강남역, null, 0, 0);
        final LineStationResponse lsr2 = new LineStationResponse(역삼역, 강남역.getId(), 10, 10);
        final LineStationResponse lsr3 = new LineStationResponse(선릉역, 역삼역.getId(), 10, 10);

        // 신분당
        final LineStationResponse lsr4 = new LineStationResponse(강남역, null, 0, 0);
        final LineStationResponse lsr5 = new LineStationResponse(양재역, 강남역.getId(), 20, 10);
        final LineStationResponse lsr6 = new LineStationResponse(양시숲, 양재역.getId(), 20, 10);

        final LineResponse lineResponse1 = createLineResponse(1L, "2호선", "GREEN", List.of(lsr1, lsr2, lsr3));
        final LineResponse lineResponse2 = createLineResponse(2L, "신분당", "RED", List.of(lsr4, lsr5, lsr6));
        lineResponses = new ArrayList<>();
        lineResponses.add(lineResponse1);
        lineResponses.add(lineResponse2);
    }

    @DisplayName("경로를 잘 찾는지 검사한다.")
    @Test
    void findPath() {

        // stubbing
        final Line line = new Line("이호선", "GREEN", LocalTime.now(), LocalTime.now(), 5);
        line.addLineStation(new LineStation(1L, null, 10, 10));
        line.addLineStation(new LineStation(2L, 1L, 10, 10));
        line.addLineStation(new LineStation(3L, 2L, 10, 10));
        when(lineRepository.findAll()).thenReturn(List.of(line));
        when(stationRepository.findAllById(anyList())).thenReturn(new ArrayList<>());

        // when
        final PathResponse pathResponse = pathService.findShortestPath(1, 3);

        // then
        assertThat(pathResponse).isNotNull();
    }

    @DisplayName("주어진 탐색 타입을 가지고 잘 찾는지 검사한다")
    @Test
    void findPathWithTypes() {

        // stubbing
        when(lineService.findAllLines()).thenReturn(lineResponses);

        // when
        final PathResponse pathResponse = pathService.findShortestPath(1, 3, FindType.DISTANCE);

        // then
        assertThat(pathResponse).isNotNull();
    }

    private LineResponse createLineResponse(long id, String name, String color, List<LineStationResponse> lineStationResponses) {
        return new LineResponse(id, name, color, LocalTime.now(), LocalTime.now(), 5, lineStationResponses, LocalDateTime.now(), LocalDateTime.now());
    }
}