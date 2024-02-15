package nextstep.subway.unit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    private static String name = "2호선";
    private static String color = "green";
    private static Long distance = 10L;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private LineService lineService;

    @Test
    void saveLine() {
        // given
        LineResponse response = 라인_생성();
        Long responseId = response.getId();
        Line line = new Line(response.getName(), response.getColor());
        when(lineRepository.findById(responseId)).thenReturn(Optional.of(line));

        // when
        Optional<Line> optionalLine = lineRepository.findById(responseId);

        // then
        assertTrue(optionalLine.isPresent());
        assertThat(response.getStations().size()).isEqualTo(2);
    }

    @Test
    void findLineById() {
        // given
        LineResponse response = 라인_생성();
        when(lineService.findLineResponseById(response.getId())).thenReturn(response);

        // when
        LineResponse lineResponseById = lineService.findLineResponseById(response.getId());

        // then
        assertThat(lineResponseById.getName()).isEqualTo(name);
        assertThat(lineResponseById.getColor()).isEqualTo(color);
    }

    @Test
    void updateLine() {
        //given
        LineResponse response = 라인_생성();

        String updateName = "1호선";
        String updateColor = "blue";
        LineRequest lineRequest = new LineRequest(updateName, updateColor, null, null, null);

        // when
        lineService.updateLine(lineRequest, response.getId());

        // then
        Line line = new LineRequest(updateName, updateColor, 1L, 2L, distance).toEntity();

        when(lineService.findLineById(response.getId())).thenReturn(line);
        Line updatedLine = lineService.findLineById(response.getId());

        assertThat(updatedLine.getName()).isEqualTo(updateName);
        assertThat(updatedLine.getColor()).isEqualTo(updateColor);
        assertNotNull(updatedLine.getDownStationId());
        assertNotNull(updatedLine.getUpStationId());
    }

    @Test
    void deleteLine() {
        //given
        LineResponse response = 라인_생성();

        // when
        when(lineRepository.findById(response.getId())).thenReturn(Optional.empty());
        when(lineService.findLineById(response.getId())).thenThrow(new IllegalArgumentException());

        lineService.deleteLine(response.getId());

        // then
        assertThrows(IllegalArgumentException.class, () -> lineService.findLineById(response.getId()));
        assertTrue(lineRepository.findById(response.getId()).isEmpty());
    }

    private LineResponse 라인_생성() {
        List<Station> stationList = List.of(
            new Station(1L),
            new Station(2L));

        when(stationRepository.saveAll(stationList)).thenReturn(stationList);

        List<Station> stations = stationRepository.saveAll(stationList);
        Station 강남역 = stations.get(0);
        Station 서초역 = stations.get(1);

        LineRequest lineRequest = new LineRequest(name, color, 강남역.getId(), 서초역.getId(), distance);
        LineResponse lineResponse = new LineResponse(1L, name, color, stations.stream()
                                                                              .map(
                                                                                  (item) -> new StationResponse(
                                                                                      item.getId(),
                                                                                      item.getName()))
                                                                              .collect(
                                                                                  Collectors.toList()));

        when(lineService.saveLine(lineRequest)).thenReturn(lineResponse);
        return lineService.saveLine(lineRequest);
    }
}
