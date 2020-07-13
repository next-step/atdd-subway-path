package nextstep.subway.map.ui;

import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("지하철 노선도 컨트롤러 테스트")
@WebMvcTest
class MapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MapController mapController;

    @MockBean
    private MapService mapService;

    @DisplayName("노선도 조회 도중 에러 발생시 에러 코드 응답")
    @Test
    void getMapsWithError() throws Exception {
        //given
        given(mapService.getMaps()).willThrow(RuntimeException.class);

        //when
        ResultActions result = mockMvc.perform(get("/maps"))
                .andDo(print());

        //then
        result.andExpect(status().is4xxClientError());
    }

    @DisplayName("노선도 조회 요청시 200 코드 응답")
    @Test
    void getMaps() {
        //when
        ResponseEntity<MapResponse> response = mapController.getMaps();

        //then
        verify(mapService).getMaps();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


}