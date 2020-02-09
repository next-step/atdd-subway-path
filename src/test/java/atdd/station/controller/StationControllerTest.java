package atdd.station.controller;

import atdd.station.dto.StationResponseDto;
import atdd.station.service.StationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @DisplayName("지하철역 등록")
    @Test
    void create() throws Exception {
        final Long id = 1L;
        final String stationName = "강남역";
        final String requestJson = "{\"name\": \"" + stationName + "\"}";

        Mockito.when(stationService.create(eq(stationName))).thenReturn(new StationResponseDto(id,
                stationName));

        mockMvc.perform(post(StationController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(stationName));
    }

    @DisplayName("지하철역 등록 - name 은 공백이나 null 일 수 없다.")
    @ParameterizedTest(name = "[{index}] stationName : [{0}]")
    @MethodSource("blankStationName")
    void create_blank_name(String stationName) throws Exception {
        final String requestJson = "{\"name\": " + stationName + "}";

        mockMvc.perform(post(StationController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    public static Stream<Arguments> blankStationName() {
        return Stream.of(
            Arguments.of("\"  \""),
            Arguments.of("\"\""),
            null
        );
    }

}
