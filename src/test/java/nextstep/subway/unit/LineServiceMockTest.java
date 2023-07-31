package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.exception.SectionAddException;
import nextstep.subway.exception.SectionDeleteException;
import nextstep.subway.exception.SectionDeleteMinSizeException;
import nextstep.subway.exception.SectionExistException;
import nextstep.subway.service.line.LineService;
import nextstep.subway.service.line.request.SectionAddRequest;
import nextstep.subway.service.line.response.LineResponse;
import nextstep.subway.service.station.response.StationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static nextstep.subway.unit.utils.LineMother.makeLine;
import static nextstep.subway.unit.utils.StationMother.makeStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private static final Station GANGNAM_STATION = makeStation("강남역");
    private static final Station SEOLLEUNG_STATION = makeStation("선릉역");
    private static final Station SUWON_STATION = makeStation("수원역");

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선에 구간을 추가하면 노선 역이름 조회시 추가되있어야 한다.")
    @Test
    void addSection() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when
        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findLine(1L).getStations()).hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("강남역", "선릉역", "수원역");
    }

    @DisplayName("지하철 노선 추가 시 구간상행역이 노선 종점역이 아니게 등록할 경우 실패되어야 한다.")
    @Test
    void upStation_not_available_addSection_fail() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(GANGNAM_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when then
        Assertions.assertThrows(
                SectionAddException.class,
                () -> lineService.addSection(1L, new SectionAddRequest(1L, 3L, 3)),
                "구간정보에 상행역이 현재 노선에 하행 종점역이 아닙니다.");
    }

    @DisplayName("지하철 노선 추가 시 구간 하행역이 이미 노선에 등록되어 있을 경우 실패되어야 한다.")
    @Test
    void exist_station_addSection_fail() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(GANGNAM_STATION));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when then
        Assertions.assertThrows(
                SectionExistException.class,
                () ->  lineService.addSection(1L, new SectionAddRequest(2L, 1L, 3)),
                "구간 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("지하철 노선에 등록된 역을 조회하면 지금까지 등록된 모든 역에 정보가 조회되야 한다.")
    @Test
    void getStations() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // when
        LineResponse lineResponse = lineService.findLine(1L);

        // then
        assertThat(lineResponse.getStations()).hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder(
                        GANGNAM_STATION.getName(),
                        SEOLLEUNG_STATION.getName(),
                        SUWON_STATION.getName()
                );
    }

    @DisplayName("지하철 노선에 구간을 삭제하면 노선 역이름 조회시 삭제한 역은 제외되야 한다.")
    @Test
    void removeSection() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // when
        lineService.deleteSection(1L, 3L);

        // then
        assertThat(lineService.findLine(1L).getStations()).hasSize(2)
                .extracting("name")
                .containsExactlyInAnyOrder(
                        GANGNAM_STATION.getName(),
                        SEOLLEUNG_STATION.getName()
                );
    }

    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간정보가 1개이므로 삭제가 실패되어야 한다.")
    @Test
    void min_section_removeSection_fail() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when then
        Assertions.assertThrows(
                SectionDeleteMinSizeException.class,
                () ->  lineService.deleteSection(1L, 2L),
                "구간이 1개인 경우 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선 구간 삭제시 하행종점역이 아닐경우 삭제가 실패되어야 한다.")
    @Test
    void not_line_downstation_removeSection_fail() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // when then
        Assertions.assertThrows(
                SectionDeleteException.class,
                () ->  lineService.deleteSection(1L, 2L),
                "구간은 종점역만 삭제가능합니다.");
    }

}
