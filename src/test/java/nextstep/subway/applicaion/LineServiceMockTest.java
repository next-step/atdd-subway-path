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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.*;
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
        // given
        Line createLine = new Line(1L, "4호선", "blue");
        when(lineRepository.save(any())).thenReturn(createLine);

        final Station station1 = new Station("중앙역");
        final Station station2 = new Station("한대앞역");

        when(stationService.findById(1L)).thenReturn(station1);
        when(stationService.findById(2L)).thenReturn(station2);
        when(stationService.createStationResponse(station1)).thenReturn(new StationResponse(1L, station1.getName()));
        when(stationService.createStationResponse(station2)).thenReturn(new StationResponse(2L, station2.getName()));

        // when
        final LineResponse lineResponse = lineService.saveLine(new LineRequest("4호선", "blue", 1L, 2L, 10));

        // then
        assertAll(
                () -> assertEquals("4호선", lineResponse.getName()),
                () -> assertEquals("blue", lineResponse.getColor()),
                () -> assertEquals(2, lineResponse.getStations().size()),
                () -> assertEquals(station1.getName(), lineResponse.getStations().get(0).getName()),
                () -> assertEquals(station2.getName(), lineResponse.getStations().get(1).getName())
        );
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
        // given
        when(stationService.findById(1L)).thenReturn(new Station("중앙역"));
        when(stationService.findById(2L)).thenReturn(new Station("한대앞역"));
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line(1L, "4호선", "blue")));

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        LineResponse lineResopnse = lineService.findById(1L);
        assertThat(lineResopnse.getStations()).hasSize(2);
    }

    @ValueSource(longs = {1, 2, 3})
    @ParameterizedTest(name = "[{argumentsWithNames}] 구간이 제거된다.")
    void deleteSectionTest(long deleteStationId) {
        // given
        Station station1 = new Station("중앙역");
        Station station2 = new Station("한대앞역");
        Station station3 = new Station("상록수역");

        when(stationService.findById(1L)).thenReturn(station1);
        when(stationService.findById(2L)).thenReturn(station2);
        when(stationService.findById(3L)).thenReturn(station3);

        when(stationService.createStationResponse(station1)).thenReturn(new StationResponse(1L, station1.getName()));
        when(stationService.createStationResponse(station2)).thenReturn(new StationResponse(2L, station2.getName()));
        when(stationService.createStationResponse(station3)).thenReturn(new StationResponse(3L, station3.getName()));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line(1L, "4호선", "blue")));

        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));
        lineService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // when
        StationResponse deleteStation = lineService.findById(1L).getStations().stream().filter(s -> s.getId().equals(deleteStationId)).findFirst().orElseThrow(IllegalArgumentException::new);
        lineService.deleteSection(1L, deleteStationId);


        // then
        LineResponse lineResponse = lineService.findById(1L);
        List<StationResponse> stations = lineResponse.getStations();
        assertAll(
                () -> assertEquals(2, stations.size()),
                () -> assertFalse(stations.contains(deleteStation))
        );
    }

}