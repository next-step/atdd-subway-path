package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PathControllerTest {

    @Test
    void findShortestPath() {
        // given
        PathService pathService = mock(PathService.class);
        PathController pathController = new PathController(pathService);
        PathRequest request = new PathRequest(1L, 3L);

        // when
        ResponseEntity response = pathController.findShortestPath(request);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(pathService).findShortestPath(request);
    }
}
