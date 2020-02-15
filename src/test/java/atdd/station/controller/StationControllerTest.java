package atdd.station.controller;

import atdd.station.dto.StationResponseDto;
import atdd.station.service.StationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StationController.class)
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

        when(stationService.create(eq(stationName))).thenReturn(new StationResponseDto(id, stationName));

        mockMvc.perform(post(StationController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(stationName));
    }

    @DisplayName("지하철역 등록 - name 은 공백이나 null 일 수 없다.")
    @ParameterizedTest(name = "[{index}] name : [{0}]")
    @MethodSource("blankBodyName")
    void create_blank_name(String name) throws Exception {
        final String requestJson = "{\"name\": " + name + "}";

        mockMvc.perform(post(StationController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    public static Stream<Arguments> blankBodyName() {
        return Stream.of(
                Arguments.of("\"  \""),
                Arguments.of("\"\""),
                null
        );
    }

    @DisplayName("지하철역 정보 조회")
    @Test
    void getStation() throws Exception {
        final Long id = 41546L;
        final String name = "stationName!!";

        when(stationService.getStation(id)).thenReturn(new StationResponseDto(id, name));

        mockMvc.perform(get(StationController.ROOT_URI + "/" + id)
                .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));

    }

    @DisplayName("지하철역 삭제 - name 은 공백이나 null 일 수 없다.")
    @ParameterizedTest(name = "[{index}] name : [{0}]")
    @NullAndEmptySource
    void delete_blank_name(String name) throws Exception {
        mockMvc.perform(delete(StationController.ROOT_URI + "/by-name")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .queryParam("name", name))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
