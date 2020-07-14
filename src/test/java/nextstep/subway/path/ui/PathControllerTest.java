package nextstep.subway.path.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.path.acceptance.PathAcceptanceTest;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.domain.PathFindType;
import nextstep.subway.path.dto.PathResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("지하철역 경로 조회하는 API Controller 테스트")
public class PathControllerTest {

    @DisplayName("최단 경로를 조회하는 API controller 테스트")
    @Test
    public void findShortestPathTest() {
        // given
        long startStationId = 1L;
        long endStationId = 2L;
        PathService pathService = mock(PathService.class);
        PathController pathController = new PathController(pathService);

        // when
        ResponseEntity<PathResponse> responseEntity = pathController.findShortestPath(startStationId, endStationId);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(pathService).findShortestPath(startStationId, endStationId, PathFindType.DISTANCE);

    }
}
