package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import org.assertj.core.api.BDDAssumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.BDDAssumptions.*;

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
    void findShortestPathWithError() {
        
    }

    @DisplayName("경로 조회 요청 시 찾은 최단 경로와 200 코드 응답")
    @Test
    void findShortestPath() {
    }
}