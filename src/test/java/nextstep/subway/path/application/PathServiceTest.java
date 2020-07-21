package nextstep.subway.path.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.ShortestPathResult;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("최단 경로를 조회하는 서비스 레이어의 단위 테스트")
public class PathServiceTest {

    @DisplayName("최단 경로를 조회하는 서비스의 메소드 호출 테스트")
    @Test
    void 최단_경로를_조회할때_서비스_메소드가_호출된다() {
        // given
        Long startId = 1L;
        Long endId = 2L;
        ShortestPathFinder shortestPathFinder = mock(ShortestPathFinder.class);
        LineService lineService = mock(LineService.class);
        when(lineService.findAllLines()).thenReturn(Collections.emptyList());
        PathService pathService = new PathService(shortestPathFinder, lineService);

        // when
        PathResponse response = pathService.findShortestPath(startId, endId, ShortestPathSearchType.DISTANCE);

        // then
        assertThat(response).isNotNull();
        verify(shortestPathFinder).findShortestPath(anyList(), anyLong(), anyLong(),
            any(ShortestPathSearchType.class));
    }

    @DisplayName("전체 노선 중 입력받은 두 역 간의 최단 경로를 반환한다.")
    @Test
    void 최단_경로를_검색해서_결과를_반환한다() {
        // given
        StationResponse 양재시민의숲역 = new StationResponse(1L, "양재시민의숲역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse 양재역 = new StationResponse(2L, "양재역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse 강남역 = new StationResponse(3L, "강남역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse 역삼역 = new StationResponse(4L, "역삼역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse 선릉역 = new StationResponse(5L, "선릉역", LocalDateTime.now(), LocalDateTime.now());

        LineStationResponse 신분당선_양재시민의숲 = new LineStationResponse(양재시민의숲역, null, 5, 5);
        LineStationResponse 신분당선_양재역 = new LineStationResponse(양재역, 양재시민의숲역.getId(), 5, 5);
        LineStationResponse 신분당선_강남역 = new LineStationResponse(강남역, 양재역.getId(), 5, 5);
        LineStationResponse 지하철2호선_강남역 = new LineStationResponse(강남역, null, 5, 5);
        LineStationResponse 지하철2호선_역삼역 = new LineStationResponse(역삼역, 강남역.getId(), 5, 5);
        LineStationResponse 지하철2호선_선릉역 = new LineStationResponse(선릉역, 역삼역.getId(), 5, 5);

        LineResponse 지하철_2호선 = PathServiceStep.테스트를_위해_시간을_고정한_LineResponse를_생성한다(1L, "지하철2호선", "GREEN", Arrays.asList(
            지하철2호선_강남역,
            지하철2호선_역삼역,
            지하철2호선_선릉역
        ));

        LineResponse 신분당선 = PathServiceStep.테스트를_위해_시간을_고정한_LineResponse를_생성한다(2L, "신분당선", "RED", Arrays.asList(
            신분당선_양재시민의숲,
            신분당선_양재역,
            신분당선_강남역
        ));

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
}
