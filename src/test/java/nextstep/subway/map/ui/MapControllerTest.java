package nextstep.subway.map.ui;

import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MapControllerTest {

    @Mock
    private MapService mapService;

    @Test
    void loadMap() {
        // given
        MapController mapController = new MapController(mapService);

        // when
        ResponseEntity<MapResponse> response = mapController.loadMap();

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
