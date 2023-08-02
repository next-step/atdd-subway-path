package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.exception.*;
import nextstep.subway.service.line.LineService;
import nextstep.subway.service.line.request.SectionAddRequest;
import nextstep.subway.service.line.response.ShortPathResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private static final Station GANGNAM_STATION = new Station("강남역");
    private static final Station SEOLLEUNG_STATION = new Station("선릉역");
    private static final Station SUWON_STATION = new Station("수원역");
    private static final Station NOWON_STATION = new Station("노원역");
    private static final Station DEARIM_STATION = new Station("대림역");

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";
    private static final String TWO_LINE_NAME = "2호선";
    private static final String TWO_LINE_COLOR = "bg-green-600";
    private static final String THREE_LINE_NAME = "3호선";
    private static final String TRHEE_LINE_COLOR = "bg-blue-600";
    private static final String FOUR_LINE_NAME = "4호선";
    private static final String FOUR_LINE_COLOR = "bg-black-600";

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
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when
        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineService.findLine(1L).getStations()).hasSize(3)
                .extracting("name")
                .containsExactly("강남역", "선릉역", "수원역");
    }

    @DisplayName("지하철 노선 추가 시 노선에 구간에 역이 둘다 존재할 경우 에러를 던진다.")
    @Test
    void section_station_all_exist_add_fail() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(GANGNAM_STATION));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionAddRequest(1L, 2L, 3)))
                .isExactlyInstanceOf(SectionExistException.class)
                .hasMessage("구간 상행역, 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("지하철 노선 추가 시 노선에 구간에 역이 둘다 존재하지 않을경우 에러를 던진다.")
    @Test
    void section_station_all_not_exist_add_fail() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(SUWON_STATION));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(NOWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionAddRequest(2L, 1L, 3)))
                .isExactlyInstanceOf(SectionNotExistException.class)
                .hasMessage("구간 상행역, 하행역이 노선에 하나도 포함되어있지 않습니다.");
    }

    @DisplayName("지하철 노선에 구간시 기존구간에 길이를 초과하면 에러를 던진다.")
    @Test
    void section_distance_over_add_fail() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(NOWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // when then
        assertThatThrownBy(() -> lineService.addSection(1L, new SectionAddRequest(2L, 4L, 5)))
                .isExactlyInstanceOf(SectionDistanceOverException.class)
                .hasMessage("구간길이를 초과했습니다.");
    }

    @DisplayName("지하철 노선에 구간을 삭제하면 노선 역이름 조회시 삭제한 역은 제외되야 한다.")
    @Test
    void removeSection() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // when
        lineService.deleteSection(1L, 3L);

        // then
        assertThat(lineService.findLine(1L).getStations()).hasSize(2)
                .extracting("name")
                .containsExactly(
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
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        // when then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 2L))
                .isExactlyInstanceOf(SectionDeleteMinSizeException.class)
                .hasMessage("구간이 1개인 경우 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간에 포함된 역이 아닌경우 삭제에 실패되어야 한다.")
    @Test
    void not_exist_station_removeSection_fail() {
        // given
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(3L)).thenReturn(Optional.of(SUWON_STATION));
        when(stationRepository.findById(4L)).thenReturn(Optional.of(NOWON_STATION));

        when(lineRepository.findById(1L)).thenReturn(Optional.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10)
        ));

        lineService.addSection(1L, new SectionAddRequest(2L, 3L, 3));

        // when then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 4L))
                .isExactlyInstanceOf(SectionNotFoundException.class)
                .hasMessage("구간정보를 찾을 수 없습니다.");
    }

    @DisplayName("강남역에서 수원역으로 가는 경로 2가지중 선릉역을 경유한 최단거리 경로를 리턴해야한다.")
    @Test
    void gangname_move_suwon() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(GANGNAM_STATION));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findAll()).thenReturn(List.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 2),
                new Line(TWO_LINE_NAME, TWO_LINE_COLOR, SEOLLEUNG_STATION, SUWON_STATION, 3),
                new Line(THREE_LINE_NAME, TRHEE_LINE_COLOR, GANGNAM_STATION, NOWON_STATION, 5),
                new Line(FOUR_LINE_NAME, FOUR_LINE_COLOR, NOWON_STATION, SUWON_STATION, 3)
        ));

        // when
        ShortPathResponse shortPathResponse = lineService.findShortPath(1L, 2L);

        // then
        assertThat(shortPathResponse.getStations())
                .hasSize(3)
                .extracting("name")
                .containsExactly(GANGNAM_STATION.getName(), SEOLLEUNG_STATION.getName(), SUWON_STATION.getName());
        assertThat(shortPathResponse.getDistance()).isEqualTo(5);
    }

    @DisplayName("선릉역에서 수원역으로 가는 경로 1가지를 리턴해야한다.")
    @Test
    void seolleung_move_suwon() {
        // given when
        when(stationRepository.findById(1L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(SUWON_STATION));

        when(lineRepository.findAll()).thenReturn(List.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 2),
                new Line(TWO_LINE_NAME, TWO_LINE_COLOR, SEOLLEUNG_STATION, SUWON_STATION, 3),
                new Line(THREE_LINE_NAME, TRHEE_LINE_COLOR, GANGNAM_STATION, NOWON_STATION, 5),
                new Line(FOUR_LINE_NAME, FOUR_LINE_COLOR, NOWON_STATION, SUWON_STATION, 3)
        ));

        // when
        ShortPathResponse shortPathResponse = lineService.findShortPath(1L, 2L);

        // then
        assertThat(shortPathResponse.getStations())
                .hasSize(2)
                .extracting("name")
                .containsExactly(SEOLLEUNG_STATION.getName(), SUWON_STATION.getName());
        assertThat(shortPathResponse.getDistance()).isEqualTo(3);
    }

    @DisplayName("최단경로 조회 역중 노선에 포함되지 않은 역이 존재할 경우 에러를 던진다.")
    @Test
    void not_exist_station_in_line() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(SEOLLEUNG_STATION));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(DEARIM_STATION));

        when(lineRepository.findAll()).thenReturn(List.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 2),
                new Line(TWO_LINE_NAME, TWO_LINE_COLOR, SEOLLEUNG_STATION, SUWON_STATION, 3),
                new Line(THREE_LINE_NAME, TRHEE_LINE_COLOR, GANGNAM_STATION, NOWON_STATION, 5),
                new Line(FOUR_LINE_NAME, FOUR_LINE_COLOR, NOWON_STATION, SUWON_STATION, 3)
        ));

        // when then
        assertThatThrownBy(() -> lineService.findShortPath(1L, 2L))
                .isExactlyInstanceOf(StationNotExistException.class)
                .hasMessage("노선에 역이 존재하지 않습니다.");
    }

    @DisplayName("최단경로 조회 시작역, 종착역이 동일할 경우 에러를 던진다.")
    @Test
    void shortpath_station_same() {
        // given
        when(stationRepository.findById(1L)).thenReturn(Optional.of(DEARIM_STATION));

        when(lineRepository.findAll()).thenReturn(List.of(
                new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 2),
                new Line(TWO_LINE_NAME, TWO_LINE_COLOR, SEOLLEUNG_STATION, SUWON_STATION, 3),
                new Line(THREE_LINE_NAME, TRHEE_LINE_COLOR, GANGNAM_STATION, NOWON_STATION, 5),
                new Line(FOUR_LINE_NAME, FOUR_LINE_COLOR, NOWON_STATION, SUWON_STATION, 3)
        ));

        // given when then
        assertThatThrownBy(() -> lineService.findShortPath(1L, 1L))
                .isExactlyInstanceOf(ShortPathSameStationException.class)
                .hasMessage("최단경로 시작역, 종착역이 동일할 수 없습니다.");
    }

}
