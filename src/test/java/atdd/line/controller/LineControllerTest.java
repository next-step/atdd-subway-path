package atdd.line.controller;

import atdd.line.domain.TimeTable;
import atdd.line.dto.LineCreateRequestDto;
import atdd.line.dto.LineResponseDto;
import atdd.line.service.LineService;
import atdd.station.dto.StationResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = LineController.class)
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineService lineService;


    @DisplayName("create")
    @Test
    void create() throws Exception {
        final String name = "name";
        final LocalTime startTime = LocalTime.MIN;
        final LocalTime endTime = LocalTime.MAX;
        final TimeTable timeTable = new TimeTable(startTime, endTime);
        final int intervalTime = 3;
        final List<StationResponseDto> stations = new ArrayList<>();

        final LineCreateRequestDto requestDto = new LineCreateRequestDto(name, startTime, endTime, intervalTime);
        final LineResponseDto responseDto = new LineResponseDto(1L, name, timeTable, intervalTime, stations);

        given(lineService.create(requestDto)).willReturn(responseDto);
        final String requestJson = objectMapper.writeValueAsString(requestDto);


        final MockHttpServletResponse response = mockMvc.perform(post(LineController.ROOT_URI)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.startTime").value(timeTable.getFormattedStartTime()))
                .andExpect(jsonPath("$.endTime").value(timeTable.getFormattedEndTime()))
                .andExpect(jsonPath("$.intervalTime").value(intervalTime))
                .andExpect(jsonPath("$.intervalTime").value(intervalTime))
                .andExpect(jsonPath("$.stations").value(stations))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void findAll() throws Exception {
        final MockHttpServletResponse response = mockMvc.perform(get(LineController.ROOT_URI))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(lineService, times(1)).findAll();
    }


}