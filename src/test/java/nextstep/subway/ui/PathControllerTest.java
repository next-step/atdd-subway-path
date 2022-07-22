package nextstep.subway.ui;

import nextstep.subway.applicaion.PathService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PathController.class)
class PathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PathService pathService;

    @Test
    void 최단거리조회() throws Exception {
        final long source = 1L;
        final long target = 2L;

        final ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/paths")
                .param("source", String.valueOf(source))
                .param("target", String.valueOf(target))
        );

        result.andExpect(status().isOk());
    }



}