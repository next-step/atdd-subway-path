package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DisplayName("PathController 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class PathControllerTest {

    @Mock
    private PathService pathService;

    private PathController pathController;

    @BeforeEach
    void setUp() {
        pathController = new PathController(pathService);
    }

    @DisplayName("정상적인 요청에 대한 응답은 OK고 경로 정보와 거리 정보를 반환한다.")
    @Test
    void response() {

        // when
        final ResponseEntity<PathResponse> shortestPath =
                pathController.getShortestPath(1L, 5L);

        // then
        assertThat(shortestPath.getStatusCode()).isEqualTo(HttpStatus.OK);
        final PathResponse pathResponse = shortestPath.getBody();
        assertThat(pathResponse).isNotNull();
        assertThat(pathResponse.getStations()).isNotNull();
        assertThat(pathResponse.getDistance()).isNotNull();
        verify(pathService).findShortestPath();
    }
}