package nextstep.subway.map.ui;

import nextstep.subway.SubwayApplication;
import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("지하철 노선도 컨트롤러 테스트")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SubwayApplication.class)
class MapControllerTest {

    @Autowired
    private MapController mapController;

    @MockBean
    private MapService mapService;

    @DisplayName("노선도 조회 도중 에러 발생시 에러 코드 응답")
    @Test
    void getMapsWithError() {
        //given
        given(mapService.getMaps()).willThrow(RuntimeException.class);

        //when
        ResponseEntity<MapResponse> response = mapController.getMaps();

        //then
        assertThat(response.getStatusCode().isError()).isTrue();
    }


}