package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PathControllerTest {
    // given
    PathService pathService;
    PathController pathController;

    @BeforeEach
    void setUp() {
        pathService = mock(PathService.class);
        pathController = new PathController(pathService);
    }

    @DisplayName("최단 경로 조회 컨트롤러 테스트")
    @Test
    void findShortestPath() {
        // given
        PathRequest request = new PathRequest(1L, 3L);

        // when
        ResponseEntity response = pathController.findShortestPath(request);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(pathService).findShortestPath(request);
    }

    @DisplayName("최단 경로 조회 출발역과 도착역이 같은 경우")
    @Test
    void findShortestPathWithSameSourceAndTarget() {
        // given
        PathRequest request = new PathRequest(1L, 1L);

        // when
        doThrow(new RuntimeException()).when(pathService).findShortestPath(request);
        ResponseEntity response = pathController.findShortestPath(request);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
