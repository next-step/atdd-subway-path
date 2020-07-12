package nextstep.subway.path.ui;

import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색 컨트롤러 테스트")
class PathControllerTest {
    private PathController pathController = new PathController();

    @DisplayName("정상의 경우 OK를 응답한다")
    @Test
    void returnsWithStatusCodeOK() {
        // when
        ResponseEntity<PathResponse> responseEntity = pathController.searchPath(new PathRequest(1L, 3L));

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("출발역과 도착역이 같은 경우 같은 경우 BadRequest를 응답한다")
    @Test
    void returnsWithStatusCodeBadRequest() {
        // when
        ResponseEntity<PathResponse> responseEntity = pathController.searchPath(new PathRequest(1L, 1L));

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}