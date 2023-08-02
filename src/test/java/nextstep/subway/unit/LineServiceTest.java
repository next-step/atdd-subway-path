package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.exception.*;
import nextstep.subway.service.line.LineService;
import nextstep.subway.service.line.request.SectionAddRequest;
import nextstep.subway.service.line.response.LineResponse;
import nextstep.subway.service.line.response.ShortPathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {

    private static final String GANGNAM_STATION_NAME = "강남역";
    private static final String SEOLLEUNG_STATION_NAME = "선릉역";
    private static final String SUWON_STATION_NAME = "수원역";
    private static final String NOWON_STATION_NAME = "노원역";
    private static final String DEARIM_STATION_NAME = "대림역";

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";
    private static final String TWO_LINE_NAME = "2호선";
    private static final String TWO_LINE_COLOR = "bg-green-600";
    private static final String THREE_LINE_NAME = "3호선";
    private static final String TRHEE_LINE_COLOR = "bg-blue-600";

    private Station GANGNAM_STATION;
    private Station SEOLLEUNG_STATION;
    private Station SUWON_STATION;
    private Station NOWON_STATION;
    private Station DEARIM_STATION;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        GANGNAM_STATION = saveStation(GANGNAM_STATION_NAME);
        SEOLLEUNG_STATION = saveStation(SEOLLEUNG_STATION_NAME);
        SUWON_STATION = saveStation(SUWON_STATION_NAME);
        NOWON_STATION = saveStation(NOWON_STATION_NAME);
        DEARIM_STATION = saveStation(DEARIM_STATION_NAME);
    }

    @DisplayName("지하철 노선에 구간을 추가하면 노선 역이름 조회시 추가되있어야 한다.")
    @Test
    void addSection() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when
        lineService.addSection(line.getId(), new SectionAddRequest(SEOLLEUNG_STATION.getId(), SUWON_STATION.getId(), 3));
        lineService.addSection(line.getId(), new SectionAddRequest(DEARIM_STATION.getId(), GANGNAM_STATION.getId(), 3));
        lineService.addSection(line.getId(), new SectionAddRequest(SEOLLEUNG_STATION.getId(), NOWON_STATION.getId(), 2));

        // then
        List<Station> stations = lineRepository.findById(line.getId())
                .orElse(null)
                .getStations();

        assertThat(stations).hasSize(5)
                .containsExactly(DEARIM_STATION, GANGNAM_STATION, SEOLLEUNG_STATION, NOWON_STATION, SUWON_STATION);
    }

    @DisplayName("지하철 노선 추가 시 노선에 구간에 역이 둘다 존재할 경우 에러를 던진다.")
    @Test
    void section_station_all_exist_add_fail() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        assertThatThrownBy(() -> lineService.addSection(line.getId(), new SectionAddRequest(GANGNAM_STATION.getId(), SEOLLEUNG_STATION.getId(), 3)))
                .isExactlyInstanceOf(SectionExistException.class)
                .hasMessage("구간 상행역, 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("지하철 노선 추가 시 노선에 구간에 역이 둘다 존재하지 않을경우 에러를 던진다.")
    @Test
    void section_station_all_not_exist_add_fail() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        assertThatThrownBy(() -> lineService.addSection(line.getId(), new SectionAddRequest(NOWON_STATION.getId(), DEARIM_STATION.getId(), 3)))
                .isExactlyInstanceOf(SectionNotExistException.class)
                .hasMessage("구간 상행역, 하행역이 노선에 하나도 포함되어있지 않습니다.");
    }

    @DisplayName("지하철 노선에 구간시 기존구간에 길이를 초과하면 에러를 던진다.")
    @Test
    void section_distance_over_add_fail() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        lineService.addSection(line.getId(), new SectionAddRequest(SEOLLEUNG_STATION.getId(), SUWON_STATION.getId(), 3));
        lineService.addSection(line.getId(), new SectionAddRequest(DEARIM_STATION.getId(), GANGNAM_STATION.getId(), 3));

        // when then
        assertThatThrownBy(() -> lineService.addSection(line.getId(), new SectionAddRequest(NOWON_STATION.getId(), SEOLLEUNG_STATION.getId(), 14)))
                .isExactlyInstanceOf(SectionDistanceOverException.class)
                .hasMessage("구간길이를 초과했습니다.");
    }

    @DisplayName("지하철 노선에 등록된 역을 조회하면 지금까지 등록된 모든 역에 정보가 조회되야 한다.")
    @Test
    void getStations() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        lineService.addSection(line.getId(), new SectionAddRequest(SEOLLEUNG_STATION.getId(), SUWON_STATION.getId(), 3));
        lineService.addSection(line.getId(), new SectionAddRequest(DEARIM_STATION.getId(), GANGNAM_STATION.getId(), 3));
        lineService.addSection(line.getId(), new SectionAddRequest(NOWON_STATION.getId(), SEOLLEUNG_STATION.getId(), 1));

        // when
        LineResponse lineResponse = lineService.findLine(line.getId());

        // then
        assertThat(lineResponse.getStations()).hasSize(5)
                .extracting("name")
                .containsExactly(
                        DEARIM_STATION.getName(),
                        GANGNAM_STATION.getName(),
                        NOWON_STATION.getName(),
                        SEOLLEUNG_STATION.getName(),
                        SUWON_STATION.getName()
                );
    }

    @DisplayName("지하철 노선에 구간을 삭제하면 노선 역이름 조회시 삭제한 역은 제외되야 한다.")
    @Test
    void removeSection() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        lineService.addSection(line.getId(), new SectionAddRequest(SEOLLEUNG_STATION.getId(), SUWON_STATION.getId(), 3));
        lineService.addSection(line.getId(), new SectionAddRequest(SUWON_STATION.getId(), NOWON_STATION.getId(), 3));

        // when
        lineService.deleteSection(line.getId(), NOWON_STATION.getId());

        // then
        assertThat(lineService.findLine(line.getId()).getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        GANGNAM_STATION.getName(),
                        SEOLLEUNG_STATION.getName(),
                        SUWON_STATION.getName()
                );
    }

    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간정보가 1개이므로 삭제가 실패되어야 한다.")
    @Test
    void min_section_removeSection_fail() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        assertThatThrownBy(() -> lineService.deleteSection(line.getId(), SEOLLEUNG_STATION.getId()))
                .isExactlyInstanceOf(SectionDeleteMinSizeException.class)
                .hasMessage("구간이 1개인 경우 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간에 포함된 역이 아닌경우 삭제에 실패되어야 한다.")
    @Test
    void not_exist_station_removeSection_fail() {
        // given
        Line line = saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);

        // when then
        assertThatThrownBy(() -> lineService.deleteSection(line.getId(), NOWON_STATION.getId()))
                .isExactlyInstanceOf(SectionNotFoundException.class)
                .hasMessage("구간정보를 찾을 수 없습니다.");
    }

    @DisplayName("강남역에서 수원역으로 가는 경로 2가지중 선릉역을 경유한 최단거리 경로를 리턴해야한다.")
    @Test
    void gangname_move_suwon() {
        // given
        saveLines();

        // when
        ShortPathResponse shortPathResponse = lineService.findShortPath(GANGNAM_STATION.getId(), SUWON_STATION.getId());

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
        saveLines();

        // when
        ShortPathResponse shortPathResponse = lineService.findShortPath(SEOLLEUNG_STATION.getId(), SUWON_STATION.getId());

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
        saveLines();

        // when then
        assertThatThrownBy(() -> lineService.findShortPath(SEOLLEUNG_STATION.getId(), DEARIM_STATION.getId()))
                .isExactlyInstanceOf(StationNotExistException.class)
                .hasMessage("노선에 역이 존재하지 않습니다.");
    }

    @DisplayName("최단경로 조회 시작역, 종착역이 동일할 경우 에러를 던진다.")
    @Test
    void shortpath_station_same() {
        // given
        saveLines();

        // given when then
        assertThatThrownBy(() -> lineService.findShortPath(DEARIM_STATION.getId(), DEARIM_STATION.getId()))
                .isExactlyInstanceOf(ShortPathSameStationException.class)
                .hasMessage("최단경로 시작역, 종착역이 동일할 수 없습니다.");
    }

    private Station saveStation(String stationName) {
        return stationRepository.save(new Station(stationName));
    }

    private Line saveLine(String name, String color, Station upStation, Station downStation, int distance) {
        return lineRepository.save(new Line(name, color, upStation, downStation, distance));
    }

    private void saveLines() {
        saveLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 2);
        saveLine(TWO_LINE_NAME, TWO_LINE_COLOR, SEOLLEUNG_STATION, SUWON_STATION, 3);
        saveLine(THREE_LINE_NAME, TRHEE_LINE_COLOR, GANGNAM_STATION, NOWON_STATION, 5).addSection(NOWON_STATION, SUWON_STATION, 3);
    }
}
