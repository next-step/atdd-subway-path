package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationService stationService;

    @InjectMocks
    LineService lineService;

    @Test
    @DisplayName("Line을 저장하면, 기대 결과가 정상적으로 나온다.")
    void saveLineTest() {
        createMockLineResponse("4호선", "blue", "중앙역", "한대앞역");
    }

    @Test
    @DisplayName("저장된 모든 Line이 조회된다.")
    void showLinesTest() {
        // given
        when(lineRepository.findAll()).thenReturn(List.of(new Line("4호선", "blue")));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).hasSize(1);
        assertThat(lineResponses.get(0).getName()).isEqualTo("4호선");

    }

    @Test
    @DisplayName("line의 값을 수정하면 정상적으로 업데이트 된다.")
    void updateLineTest() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("4호선", "blue")));

        // when
        lineService.updateLine(1L, new LineRequest("2호선", "green"));

        // then
        LineResponse lineResponse = lineService.findById(1L);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");

    }

    @Test
    @DisplayName("Line을 삭제하면 deleteById가 호출되며 정상적으로 삭제된다.")
    void deleteLineTest() {

        // when
        lineService.deleteLine(1L);
        verify(lineRepository, times(1)).deleteById(1L);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> lineService.findById(1L));
    }

    @Test
    @DisplayName("Line에 역을 추가하면 추가한 역이 함께 조회가 된다.")
    @Transactional
    void addSectionTest() {
        LineResponse mockLineResponse = createMockLineResponse("4호선", "blue", "중앙역", "한대앞역");
        addStation(mockLineResponse, "상록수역");
    }

    @Test
    void deleteSectionTest() {
        LineResponse mockLineResponse = createMockLineResponse("4호선", "blue", "중앙역", "한대앞역");
        addStation(mockLineResponse, "상록수역");

        // then
        lineService.deleteSection(1L, 3L);

        LineResponse lineResponse3 = lineService.findById(1L);

        assertThat(lineResponse3.getStations()).hasSize(2);
        assertThat(lineResponse3.getStations()).doesNotContain(new StationResponse(3L, "상록수역"));
    }

    private LineResponse createMockLineResponse(String lineName, String lineColor, String stationsName1, String stationName2) {
        // given
        Line createLine = new Line(1L, lineName, lineColor);
        when(lineRepository.save(any())).thenReturn(createLine);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(createLine));

        Station station1 = new Station(stationsName1);
        Station station2 = new Station(stationName2);

        when(stationService.findById(1L)).thenReturn(station1);
        when(stationService.findById(2L)).thenReturn(station2);
        when(stationService.createStationResponse(station1)).thenReturn(new StationResponse(1L, station1.getName()));
        when(stationService.createStationResponse(station2)).thenReturn(new StationResponse(2L, station2.getName()));

        // when
        LineResponse lineResponse = lineService.saveLine(new LineRequest(lineName, lineColor, 1L, 2L, 10));

        // then
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
        assertThat(lineResponse.getStations()).hasSize(2);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(station1.getName());
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(station2.getName());


        return lineResponse;
    }

    private void addStation(LineResponse lineResponse, String addStationName) {

        Station newStation = new Station(addStationName);
        long newStationId = lineResponse.getStations().size() + 1;

        when(stationService.findById(newStationId)).thenReturn(newStation);
        lineService.addSection(lineResponse.getId(), new SectionRequest((long) lineResponse.getStations().size(), newStationId, 10));

        when(stationService.createStationResponse(newStation)).thenReturn(new StationResponse(newStationId, newStation.getName()));
        LineResponse lineResponse2 = lineService.findById(lineResponse.getId());

        assertThat(lineResponse2.getStations()).hasSize(lineResponse.getStations().size() + 1);
        assertThat(lineResponse2.getStations().get(getLastIndex(lineResponse2)).getName()).isEqualTo(newStation.getName());
    }

    private int getLastIndex(LineResponse lineResponse2) {
        return lineResponse2.getStations().size() - 1;
    }

}