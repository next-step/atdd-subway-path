package nextstep.subway.map.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.map.ui.MapController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MapControllerTest {

    @DisplayName("지하철 노선도를 조회한다")
    @Test
    public void getLineMap() {
        // given
        LineService lineService = mock(LineService.class);
        MapController mapController = new MapController(lineService);

        // when
        ResponseEntity<MapResponse> responseEntity = mapController.getLineMap();

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(lineService).findAllLines();
    }
}
