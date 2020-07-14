package nextstep.subway.map.ui;

import nextstep.subway.line.application.LineService;
import nextstep.subway.map.dto.MapResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@DisplayName("MapController 유닛 테스트")
@ExtendWith(MockitoExtension.class)
class MapControllerTest {

    @Mock
    private LineService lineService;

    private MapController mapController;

    @BeforeEach
    void setUp() {
        mapController =new MapController(lineService);
    }

    @DisplayName("모든 노선의 정보를 응답한다.")
    @Test
    void fetch_maps() {

        // when
        final ResponseEntity<MapResponse> result = mapController.getMap();

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(lineService).findAllLines();
    }
}