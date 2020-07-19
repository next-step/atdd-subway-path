package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class PathControllerTest {

    @Test
    void findShortestPath() {
        // given
        PathController pathController = new PathController();

        // when
        ResponseEntity response = pathController.findShortestPath(
                new PathRequest(1L, 3L)
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
