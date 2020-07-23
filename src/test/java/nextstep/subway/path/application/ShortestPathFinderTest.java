package nextstep.subway.path.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.ShortestPathResult;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("최단 경로를 조회하는 서비스 레이어의 단위 테스트")
public class ShortestPathFinderTest {

    private StationResponse 양재시민의숲역;
    private StationResponse 양재역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 선릉역;
    private LineStationResponse 신분당선_양재시민의숲;
    private LineStationResponse 신분당선_양재역;
    private LineStationResponse 신분당선_강남역;
    private LineStationResponse 지하철2호선_강남역;
    private LineStationResponse 지하철2호선_역삼역;
    private LineStationResponse 지하철2호선_선릉역;
    private LineResponse 지하철_2호선;
    private LineResponse 신분당선;

    @BeforeEach
    void setUp() {
        // given
        양재시민의숲역 = new StationResponse(1L, "양재시민의숲역", LocalDateTime.now(), LocalDateTime.now());
        양재역 = new StationResponse(2L, "양재역", LocalDateTime.now(), LocalDateTime.now());
        강남역 = new StationResponse(3L, "강남역", LocalDateTime.now(), LocalDateTime.now());
        역삼역 = new StationResponse(4L, "역삼역", LocalDateTime.now(), LocalDateTime.now());
        선릉역 = new StationResponse(5L, "선릉역", LocalDateTime.now(), LocalDateTime.now());

        신분당선_양재시민의숲 = new LineStationResponse(양재시민의숲역, null, 5, 5);
        신분당선_양재역 = new LineStationResponse(양재역, 양재시민의숲역.getId(), 5, 5);
        신분당선_강남역 = new LineStationResponse(강남역, 양재역.getId(), 5, 5);
        지하철2호선_강남역 = new LineStationResponse(강남역, null, 5, 5);
        지하철2호선_역삼역 = new LineStationResponse(역삼역, 강남역.getId(), 5, 5);
        지하철2호선_선릉역 = new LineStationResponse(선릉역, 역삼역.getId(), 5, 5);

        지하철_2호선 = PathServiceStep.테스트를_위해_시간을_고정한_LineResponse를_생성한다(1L, "지하철2호선", "GREEN", Arrays.asList(
            지하철2호선_강남역,
            지하철2호선_역삼역,
            지하철2호선_선릉역
        ));

        신분당선 = PathServiceStep.테스트를_위해_시간을_고정한_LineResponse를_생성한다(2L, "신분당선", "RED", Arrays.asList(
            신분당선_양재시민의숲,
            신분당선_양재역,
            신분당선_강남역
        ));
    }

    @DisplayName("전체 노선 중 입력받은 두 역 간의 최단 경로를 반환한다.")
    @Test
    void 최단_경로를_검색해서_결과를_반환한다() {
        // when
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder();
        ShortestPathResult shortestPathResult = shortestPathFinder.findShortestPath(
            Arrays.asList(지하철_2호선, 신분당선),
            양재시민의숲역.getId(),
            선릉역.getId(),
            ShortestPathSearchType.DISTANCE
        );

        // then
        assertThat(shortestPathResult.getStations()).containsExactly(양재시민의숲역, 양재역, 강남역, 역삼역, 선릉역);
        assertThat(shortestPathResult.getWeight()).isEqualTo(20);
    }

    @DisplayName("전체 노선 중 입력받은 두 역 간의 최단 시간을 반환한다.")
    @Test
    void 최단_시간을_검색해서_결과를_반환한다() {
        // when
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder();
        ShortestPathResult shortestPathResult = shortestPathFinder.findShortestPath(
            Arrays.asList(지하철_2호선, 신분당선),
            양재시민의숲역.getId(),
            선릉역.getId(),
            ShortestPathSearchType.DURATION
        );

        // then
        assertThat(shortestPathResult.getStations()).containsExactly(양재시민의숲역, 양재역, 강남역, 역삼역, 선릉역);
        assertThat(shortestPathResult.getWeight()).isEqualTo(20);
    }
}
