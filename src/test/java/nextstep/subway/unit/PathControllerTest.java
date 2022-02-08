package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.PathController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PathController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PathControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PathService pathService;

    Long source = 1L;
    Long target = 2L;
    int distanse = 10;
    List<StationResponse> stations = new ArrayList<>();
    PathResponse response;

    @BeforeEach
    void setup() {
        stations.add(StationResponse.of(new Station("강남역")));
        stations.add(StationResponse.of(new Station("양재역")));
        response = new PathResponse(stations, distanse);
    }

    @Test
    @DisplayName("출발역과 도착역으로 경로의 최단 거리 검색")
    void searchShortestPaths() throws Exception {
        // given
        given(pathService.searchShortestPaths(source, target)).willReturn(response);

        // when
        ResultActions perform = mockMvc.perform(
                get("/paths?source={source}&target={target}", source, target));

        // then
        then(pathService).should(times(1)).searchShortestPaths(source, target);
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.stations.*", hasSize(stations.size())))
                .andExpect(jsonPath("$.distance", is(distanse)));
    }
}