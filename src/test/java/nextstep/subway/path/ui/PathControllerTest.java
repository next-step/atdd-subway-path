package nextstep.subway.path.ui;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.application.ShortestPathSearchType;
import nextstep.subway.path.dto.PathResponse;

public class PathControllerTest {

    PathService mockPathService = mock(PathService.class);
    PathController mockPathController = new PathController(mockPathService);

    @DisplayName("지하철역 최단경로를 조회하는 컨트롤러 테스트를 수행한다.")
    @Test
    public void 지하철역_최단경로_컨트롤러_테스트() {
        // given
        Long startId = 1L;
        Long endId = 2L;

        // when
        ResponseEntity<PathResponse> response = mockPathController.findShortestPath(startId, endId,
            ShortestPathSearchType.DURATION);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.OK.value());
        verify(mockPathService).findShortestPath(startId, endId, ShortestPathSearchType.DURATION);
    }
}
