package nextstep.subway.map.ui;

import nextstep.subway.map.application.PathService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PathControllerTest {

    private PathService pathService;
    private PathController pathController;

    @BeforeEach
    void setUp() {
        pathService = mock(PathService.class);
        pathController = new PathController(pathService);
    }

    @Test
    void findShortDistance() {
        // when
        ResponseEntity entity = pathController.findShortPath(1L, 3L);

        // then
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(pathService).findPath(1L, 3L);
    }
}
