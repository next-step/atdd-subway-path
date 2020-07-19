package nextstep.subway.path.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("최단 경로의 지하철 역을 찾아주는 기능 테스트")
class PathFinderTest {

    private StationResponse 홍대입구;
    private StationResponse 신촌;
    private StationResponse 이대;
    private StationResponse 아현;
    private StationResponse 충정로;
    private StationResponse 서강대;
    private StationResponse 공덕역;
    private PathFinder pathfinder;
    private List<LineResponse> allLines;
    private LineResponse 지하철2호선;
    private LineResponse 경의중앙선;
    private LineResponse 공항철도;
    private LineResponse 지하철5호선;

    @BeforeEach
    public void setUp() {
        // 역
        홍대입구 = new StationResponse(1L, "홍대입구", LocalDateTime.now(), LocalDateTime.now());
        신촌 = new StationResponse(2L, "신촌", LocalDateTime.now(), LocalDateTime.now());
        이대 = new StationResponse(3L, "이대", LocalDateTime.now(), LocalDateTime.now());
        아현 = new StationResponse(4L, "아현", LocalDateTime.now(), LocalDateTime.now());
        충정로 = new StationResponse(5L, "충정로", LocalDateTime.now(), LocalDateTime.now());
        서강대 = new StationResponse(6L, "서강대", LocalDateTime.now(), LocalDateTime.now());
        공덕역 = new StationResponse(7L, "공덕역", LocalDateTime.now(), LocalDateTime.now());

        // 노선
        // 2호선
        LineStationResponse 지하철2호선_홍대입구 = new LineStationResponse(홍대입구, null, 5, 5);
        LineStationResponse 지하철2호선_신촌 = new LineStationResponse(신촌, 홍대입구.getId(), 5, 5);
        LineStationResponse 지하철2호선_이대 = new LineStationResponse(이대, 신촌.getId(), 5, 5);
        LineStationResponse 지하철2호선_아현 = new LineStationResponse(아현, 이대.getId(), 5, 5);
        LineStationResponse 지하철2호선_충정로 = new LineStationResponse(충정로, 아현.getId(), 5, 5);

        // 경의중앙선
        LineStationResponse 경의중앙선_홍대입구 = new LineStationResponse(홍대입구, null, 7, 7);
        LineStationResponse 경의중앙선_서강대 = new LineStationResponse(서강대, 홍대입구.getId(), 7, 7);
        LineStationResponse 경의중앙선_공덕역 = new LineStationResponse(공덕역, 서강대.getId(), 7, 7);

        // 공항철도
        LineStationResponse 공항철도_홍대입구 = new LineStationResponse(홍대입구, null, 9, 9);
        LineStationResponse 공항철도_공덕역 = new LineStationResponse(공덕역, 홍대입구.getId(), 9, 9);

        // 지하철 5호선
        LineStationResponse 지하철5호선_공덕역 = new LineStationResponse(공덕역, null, 10, 10);
        LineStationResponse 지하철5호선_충정로 = new LineStationResponse(공덕역, 충정로.getId(), 10, 10);

        // 노선 등록
        지하철2호선 = new LineResponse(1L, "지하철2호선", "SKY_BLUE", LocalTime.now(), LocalTime.now(), 5, Arrays.asList(지하철2호선_홍대입구, 지하철2호선_신촌, 지하철2호선_이대, 지하철2호선_아현, 지하철2호선_충정로), LocalDateTime.now(), LocalDateTime.now());
        경의중앙선 = new LineResponse(2L, "경의중앙선", "SKY_BLUE", LocalTime.now(),LocalTime.now(), 5, Arrays.asList(경의중앙선_홍대입구, 경의중앙선_서강대, 경의중앙선_공덕역), LocalDateTime.now(), LocalDateTime.now());
        공항철도 = new LineResponse(3L, "공항철도", "SKY_BLUE", LocalTime.now(),LocalTime.now(), 5, Arrays.asList(공항철도_홍대입구, 공항철도_공덕역), LocalDateTime.now(), LocalDateTime.now());
        지하철5호선 = new LineResponse(4L, "지하철5호선", "SKY_BLUE", LocalTime.now(),LocalTime.now(), 5, Arrays.asList(지하철5호선_공덕역, 지하철5호선_충정로), LocalDateTime.now(), LocalDateTime.now());

        pathfinder = new PathFinder();
        allLines = Arrays.asList(지하철2호선, 경의중앙선, 공항철도, 지하철5호선);
    }

    /**
     * PathFinder 기능
     * - 전체 노선도와 출발지, 도착지를 입력과 경로 탐색의 기준(거리, 시간)을 입력하면 최단 경로를 찾아준다
     */
    @DisplayName("전체 노선 중 입력받은 두 역간의 최단 경로를 찾는다")
    @Test
    void findShortestPathByDistance() {
        // when
        ShortestPathResult shortestPathResult = pathfinder.findShortestPath(allLines, 홍대입구.getId(), 충정로.getId(), PathFindType.DISTANCE);

        // then
        assertThat(shortestPathResult.getStations()).containsExactly(홍대입구, 공덕역, 충정로);
        assertThat(shortestPathResult.getWeight()).isEqualTo(19);
    }

    @DisplayName("전체 노선 중 입력받은 두 역간의 최단 경로를 찾는다")
    @Test
    void findShortestPathByDuration() {
        // when
        ShortestPathResult shortestPathResult = pathfinder.findShortestPath(allLines, 홍대입구.getId(), 충정로.getId(), PathFindType.DURATION);

        // then
        assertThat(shortestPathResult.getStations()).containsExactly(홍대입구, 공덕역, 충정로);
        assertThat(shortestPathResult.getWeight()).isEqualTo(19);
    }
}