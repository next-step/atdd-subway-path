package nextstep.subway.path.ui;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.path.application.PathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("경로 검색 컨트롤러 테스트")
@SpringBootTest
@AutoConfigureMockMvc
class PathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PathController pathController;

    @MockBean
    private PathService pathService;

    @DisplayName("경로 조회 도중 에러 발생시 에러 코드 응답")
    @Test
    void findShortestPathWithError() throws Exception {
        //given
        given(pathService.findShortestPath(anyLong(), anyLong()))
                .willThrow(NotFoundException.class);

        //when
        ResultActions result = mockMvc.perform(
                get("/paths/shortest")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("startStationId", "1")
                        .param("endStationId", "2"))
                .andDo(print());

        //then
        result.andExpect(status().is4xxClientError());
    }

    @DisplayName("경로 조회 요청 시 찾은 최단 경로와 200 코드 응답")
    @Test
    void findShortestPath() {
        //when
        pathController.findShortestPath(1L, 2L);

        //then
        verify(pathService).findShortestPath(1L, 2L);
    }
}