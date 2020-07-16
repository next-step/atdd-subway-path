package nextstep.subway.map.ui;

import nextstep.subway.map.application.MapService;
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

@ExtendWith(MockitoExtension.class)
class MapControllerTest {

    @Mock
    private MapService mapService;
    private MapController controller;


    @BeforeEach
    void setUp() {
        controller = new MapController(mapService);
    }

    @DisplayName("정상 작동 시 200 코드 응답")
    @Test
    void createLineStation() {
        // when
        ResponseEntity entity = controller.loadMap();
        // then
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(mapService).findAllLineAndStation();
    }

}

