package nextstep.subway.application;

import nextstep.subway.application.dto.LineRequest;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.application.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private final Long lineId = 1L;
    private final String lineName = "2호선";
    private final String lineColor = "green";
    private final Long upStationId = 1L;
    private final String upStationName = "강남역";
    private final Long downStationId = 2L;
    private final String downStationName = "역삼역";
    private final int distance = 10;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        // given
        when(lineRepository.save(any())).thenReturn(new Line(lineName, lineColor));
        when(stationService.findById(upStationId)).thenReturn(new Station(upStationName));
        when(stationService.findById(downStationId)).thenReturn(new Station(downStationName));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        LineResponse lineResponse = lineService.saveLine(new LineRequest(lineName, lineColor, upStationId, downStationId, distance));
        List<String> stations = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineName),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineColor),
                () -> assertThat(stations).isEqualTo(Arrays.asList(upStationName, downStationName))
        );
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        // given
        Line line = new Line(lineName, lineColor);
        line.getSections().add(new Section(line, new Station(upStationName), new Station(downStationName), distance));
        when(lineRepository.findAll()).thenReturn(List.of(line));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        LineResponse lineResponse = lineService.showLines().stream().findAny().get();
        List<String> stations = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineName),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineColor),
                () -> assertThat(stations).isEqualTo(Arrays.asList(upStationName, downStationName))
        );
    }

    @DisplayName("지하철 노선 단건 조회")
    @Test
    void findById() {
        // given
        Line line = new Line(lineName, lineColor);
        line.getSections().add(new Section(line, new Station(upStationName), new Station(downStationName), distance));
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        LineResponse lineResponse = lineService.findById(lineId);
        List<String> stations = lineResponse.getStations().stream().map(StationResponse::getName).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineName),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineColor),
                () -> assertThat(stations).isEqualTo(Arrays.asList(upStationName, downStationName))
        );
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Line line = new Line(lineName, lineColor);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        String newLineName = "신분당선";
        String newLineColor = "red";
        lineService.updateLine(lineId, new LineRequest(newLineName, newLineColor, upStationId, downStationId, distance));

        // then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo(newLineName),
                () -> assertThat(line.getColor()).isEqualTo(newLineColor)
        );
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        // when
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.deleteLine(lineId);

        // then
        verify(lineRepository).deleteById(lineId);
    }

    @DisplayName("지하철 노선 내 구간 추가")
    @Test
    void addSection() {
        // given
        Line line = new Line(lineName, lineColor);
        line.setId(lineId);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        Station upStation = new Station(upStationName);
        upStation.setId(upStationId);
        when(stationService.findById(upStationId)).thenReturn(upStation);
        Station downStation = new Station(downStationName);
        downStation.setId(downStationId);
        when(stationService.findById(downStationId)).thenReturn(downStation);

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));

        // then
        Section section = line.getSections().stream().findAny().get();
        assertAll(
                () -> assertThat(section.getLine()).isEqualTo(line),
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation),
                () -> assertThat(section.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("지하철 노선 내 구간 삭제")
    @Test
    void deleteSection() {
        // given
        Line line = new Line(lineName, lineColor);
        line.setId(lineId);
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        Station upStation = new Station(upStationName);
        upStation.setId(upStationId);
        Station downStation = new Station(downStationName);
        downStation.setId(downStationId);
        Long newStationId = 999L;
        Station newStation = new Station("사당역");
        newStation.setId(newStationId);
        when(stationService.findById(newStationId)).thenReturn(newStation);
        line.getSections().addAll(List.of(
                new Section(line, upStation, downStation, distance),
                new Section(line, downStation, newStation, distance)
        ));

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.deleteSection(lineId, newStationId);

        // then
        List<Station> stations = line.getStations();
        assertAll(
                () -> assertThat(stations).containsAll(List.of(upStation, downStation)),
                () -> assertThat(stations).doesNotContain(newStation)
        );
    }
}
