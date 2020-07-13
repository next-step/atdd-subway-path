package nextstep.subway.map.ui;

import nextstep.subway.map.dto.MapResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선도 컨트롤러 테스트")
class MapControllerTest {

    @DisplayName("노선도 조회 도중 에러 발생시 에러 코드 응답")
    @Test
    void getMapsWithError() {
        //given
        MapController mapController = new MapController();

        //when
        ResponseEntity<MapResponse> response = mapController.getMaps();

        //then
        assertThat(response.getStatusCode().isError()).isTrue();
    }
}