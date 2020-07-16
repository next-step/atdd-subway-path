package nextstep.subway.path.domain;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.application.PathService;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 지도 도메인 테스트")
class PathMapTest {

    private List<LineResponse> lineResponses;

    @BeforeEach
    void setUp() {
        //given
        StationResponse stationResponse1 = new StationResponse(1L, "강남역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse2 = new StationResponse(2L, "역삼역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse3 = new StationResponse(3L, "선릉역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse4 = new StationResponse(4L, "양재역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse5 = new StationResponse(5L, "남부터미널역", LocalDateTime.now(), LocalDateTime.now());

        LineStationResponse lineStation1 = new LineStationResponse(stationResponse1, null, 0, 0);
        LineStationResponse lineStation2 = new LineStationResponse(stationResponse2, 1L, 5, 5);
        LineStationResponse lineStation3 = new LineStationResponse(stationResponse3, 2L, 5, 5);
        LineStationResponse lineStation4 = new LineStationResponse(stationResponse3, null, 0, 0);
        LineStationResponse lineStation5 = new LineStationResponse(stationResponse4, 3L, 5, 5);
        LineStationResponse lineStation6 = new LineStationResponse(stationResponse5, null, 0, 0);

        LineResponse lineResponse1 = new LineResponse(1L, "2호선", "GREEN",
                LocalTime.now(), LocalTime.now(), 5,
                Lists.list(lineStation1, lineStation2, lineStation3), LocalDateTime.now(), LocalDateTime.now());
        LineResponse lineResponse2 = new LineResponse(2L, "신분당선", "RED",
                LocalTime.now(), LocalTime.now(), 5,
                Lists.list(lineStation4, lineStation5), LocalDateTime.now(), LocalDateTime.now());
        LineResponse lineResponse3 = new LineResponse(3L, "3호선", "ORANGE",
                LocalTime.now(), LocalTime.now(), 5,
                Lists.list(lineStation6), LocalDateTime.now(), LocalDateTime.now());

        lineResponses = Lists.list(lineResponse1, lineResponse2, lineResponse3);
    }

    @Test
    @DisplayName("최단 경로를 조회한다")
    void findDijkstraShortestPath() {
        //given
        PathMap pathMap = PathMap.of(lineResponses);

        //when
        List<Long> shortestPath = pathMap.findDijkstraShortestPath(1L, 4L);

        //then
        assertThat(shortestPath).hasSize(4)
                .containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("최단 경로를 조회할 때 경로 지도에 존재하지 않는 지하철역으로 요청이 들어오면 에러를 던진다")
    void findDijkstraShortestPathWithNotFoundException() {
        //given
        PathMap pathMap = PathMap.of(lineResponses);

        //when
        assertThatThrownBy(() -> pathMap.findDijkstraShortestPath(8L, 4L))
                //then
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("최단 경로를 조회할 때 경로 지도에서 서로 이어지지 않은 지하철역으로 요청이 들어오면 에러를 던진다")
    void findDijkstraShortestPathWithNotConnectedStations() {
        //given
        PathMap pathMap = PathMap.of(lineResponses);

        //when
        assertThatThrownBy(() -> pathMap.findDijkstraShortestPath(1L, 5L))
                //then
                .isInstanceOf(NoPathExistsException.class);
    }


}