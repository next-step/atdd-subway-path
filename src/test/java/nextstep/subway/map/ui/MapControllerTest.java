package nextstep.subway.map.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nextstep.subway.line.application.LineService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MapControllerTest {
    @Test
    @DisplayName("Map 컨틀롤러는 LineService에 맵정보(모든 노선정보)를 요청한다.")
    void getMaps() {
        // given
        LineService lineService = mock(LineService.class);
        MapController controller = new MapController(lineService);

        // when
        ResponseEntity entity = controller.getMaps();
        
        // then
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(lineService).findAllLinesForMaps();        
    }    
}