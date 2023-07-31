package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.SectionAddException;
import nextstep.subway.exception.SectionDeleteException;
import nextstep.subway.exception.SectionDeleteMinSizeException;
import nextstep.subway.exception.SectionExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.utils.LineMother.makeLine;
import static nextstep.subway.unit.utils.StationMother.makeStation;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private static final Station GANGNAM_STATION = makeStation("강남역");
    private static final Station SEOLLEUNG_STATION = makeStation("선릉역");
    private static final Station SUWON_STATION = makeStation("수원역");
    private static final Station NOWON_STATION = makeStation("노원역");
    private static final Station DEARIM_STATION = makeStation("대림역");

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";

    @DisplayName("지하철 노선에 구간을 추가하면 노선 역이름 조회시 추가되있어야 한다.")
    @Test
    void addSection() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);

        // then
        assertThat(line.unmodifiableStations()).containsExactly(GANGNAM_STATION, SEOLLEUNG_STATION, SUWON_STATION);
    }

    @DisplayName("지하철 노선 추가 시 구간상행역이 노선 종점역이 아니게 등록할 경우 실패되어야 한다.")
    @Test
    void upStation_not_available_addSection_fail() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        Assertions.assertThrows(
                SectionAddException.class,
                () ->  line.addSection(GANGNAM_STATION, SUWON_STATION, 3),
                "구간정보에 상행역이 현재 노선에 하행 종점역이 아닙니다.");
    }

    @DisplayName("지하철 노선 추가 시 구간 하행역이 이미 노선에 등록되어 있을 경우 실패되어야 한다.")
    @Test
    void exist_station_addSection_fail() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        Assertions.assertThrows(
                SectionExistException.class,
                () ->  line.addSection(SEOLLEUNG_STATION, GANGNAM_STATION, 3),
                "구간 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("지하철 노선에 등록된 역을 조회하면 지금까지 등록된 모든 역에 정보가 조회되야 한다.")
    @Test
    void getStations() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);
        line.addSection(SUWON_STATION, NOWON_STATION, 4);
        line.addSection(NOWON_STATION, DEARIM_STATION, 5);

        // when
        List<Station> stations = line.unmodifiableStations();

        // then
        assertThat(stations).hasSize(5);
        assertThat(stations).containsExactly(GANGNAM_STATION, SEOLLEUNG_STATION, SUWON_STATION, NOWON_STATION, DEARIM_STATION);
    }

    @DisplayName("지하철 노선에 구간을 삭제하면 노선 역이름 조회시 삭제한 역은 제외되야 한다.")
    @Test
    void removeSection() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);
        line.addSection(SUWON_STATION, NOWON_STATION, 4);

        // when
        line.removeSection(NOWON_STATION);

        // then
        assertThat(line.unmodifiableStations()).containsExactly(GANGNAM_STATION, SEOLLEUNG_STATION, SUWON_STATION);
    }

    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간정보가 1개이므로 삭제가 실패되어야 한다.")
    @Test
    void min_section_removeSection_fail() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        Assertions.assertThrows(
                SectionDeleteMinSizeException.class,
                () ->  line.removeSection(SEOLLEUNG_STATION),
                "구간이 1개인 경우 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선 구간 삭제시 하행종점역이 아닐경우 삭제가 실패되어야 한다.")
    @Test
    void not_line_downstation_removeSection_fail() {
        // given
        Line line = makeLine(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);

        // when then
        Assertions.assertThrows(
                SectionDeleteException.class,
                () ->  line.removeSection(SEOLLEUNG_STATION),
                "구간은 종점역만 삭제가능합니다.");
    }
}
