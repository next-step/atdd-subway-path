package nextstep.subway.path.application;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.domain.LineStations;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("경로 탐색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private LineService lineService;
    private PathService pathService;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineService);
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 같은 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithSameStartAndEndStation() {
        //when
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 1L))
                //then
                .isInstanceOf(NotValidRequestException.class);
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 연결이 되어 있지 않은 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithNotConnectedStations() {
        //given
        LineStations lineStations1 = mock(LineStations.class);
        LineStations lineStations2 = mock(LineStations.class);
        LineStation lineStation1 = new LineStation(1L, null, 5, 5);
        LineStation lineStation2 = new LineStation(2L, 1L, 5, 5);
        LineStation lineStation4 = new LineStation(3L, null, 5, 5);
        LineStation lineStation5 = new LineStation(4L, 3L, 5, 5);

        given(lineStations1.getStationsInOrder())
                .willReturn(Lists.list(lineStation1, lineStation2));
        given(lineStations2.getStationsInOrder())
                .willReturn(Lists.list(lineStation4, lineStation5));

        Line line1 = reflectionLine(1L, "2호선", "GREEN", lineStations1);
        Line line2 = reflectionLine(2L, "신분당선", "RED", lineStations2);


        given(lineService.findAllLineEntities())
                .willReturn(Lists.list(line1, line2));

        //when
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 4L))
                //then
                .isInstanceOf(NoPathExistsException.class);

    }

    private Line reflectionLine(long id, String name, String color, LineStations lineStations) {
        Line line = new Line();
        ReflectionTestUtils.setField(line, "id", id);
        ReflectionTestUtils.setField(line, "name", name);
        ReflectionTestUtils.setField(line, "color", color);
        ReflectionTestUtils.setField(line, "startTime", LocalTime.now());
        ReflectionTestUtils.setField(line, "endTime", LocalTime.now());
        ReflectionTestUtils.setField(line, "intervalTime", 4);
        ReflectionTestUtils.setField(line, "lineStations", lineStations);
        return line;
    }

    @DisplayName("최단 경로 탐색 요청 시, 존재하지 않은 출발역이나 도착역을 조회 할 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithNotExistStations() {
        //when
        assertThatThrownBy(() -> pathService.findShortestPath(1L, 4L))
                //then
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("최단 경로 탐색하여 최단경로를 리턴한다.")
    @Test
    void findShortestPath() {
    }

    @DisplayName("최단 경로 탐색하여 여러개의 최단 경로가 나와도 하나의 최단 경로만 리턴한다.")
    @Test
    void findShortestPathWithMultipleAnswers() {
    }
}