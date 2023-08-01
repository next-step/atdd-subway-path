package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.*;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private static final Station GANGNAM_STATION = new Station("강남역");
    private static final Station SEOLLEUNG_STATION = new Station("선릉역");
    private static final Station SUWON_STATION = new Station("수원역");
    private static final Station NOWON_STATION = new Station("노원역");
    private static final Station DEARIM_STATION = new Station("대림역");

    private static final String SHINBUNDANG_LINE_NAME = "신분당선";
    private static final String SHINBUNDANG_LINE_COLOR = "bg-red-600";

    @DisplayName("지하철 노선에 구간을 추가하면 노선 역이름 조회시 추가되있어야 한다.")
    @Test
    void addSection() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);
        line.addSection(DEARIM_STATION, GANGNAM_STATION, 3);
        line.addSection(SEOLLEUNG_STATION, NOWON_STATION, 2);

        // then
        assertThat(line.unmodifiableStations())
                .containsExactly(DEARIM_STATION, GANGNAM_STATION, SEOLLEUNG_STATION, NOWON_STATION, SUWON_STATION);
    }

    @DisplayName("지하철 노선 추가 시 노선에 구간에 역이 둘다 존재할 경우 에러를 던진다.")
    @Test
    void section_station_all_exist_add_fail() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        assertThatThrownBy(() -> line.addSection(GANGNAM_STATION, SEOLLEUNG_STATION, 3))
                .isExactlyInstanceOf(SectionExistException.class)
                .hasMessage("구간 상행역, 하행역이 이미 노선에 등록되어 있습니다.");
    }

    @DisplayName("지하철 노선 추가 시 노선에 구간에 역이 둘다 존재하지 않을경우 에러를 던진다.")
    @Test
    void section_station_all_not_exist_add_fail() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        assertThatThrownBy(() -> line.addSection(NOWON_STATION, DEARIM_STATION, 3))
                .isExactlyInstanceOf(SectionNotExistException.class)
                .hasMessage("구간 상행역, 하행역이 노선에 하나도 포함되어있지 않습니다.");
    }

    @DisplayName("지하철 노선에 구간시 기존구간에 길이를 초과하면 에러를 던진다.")
    @Test
    void section_distance_over_add_fail() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);
        line.addSection(DEARIM_STATION, GANGNAM_STATION, 3);

        // when then
        assertThatThrownBy(() -> line.addSection(NOWON_STATION, SEOLLEUNG_STATION, 14))
                .isExactlyInstanceOf(SectionDistanceOverException.class)
                .hasMessage("구간길이를 초과했습니다.");
    }

    @DisplayName("지하철 노선에 등록된 역을 조회하면 지금까지 등록된 모든 역에 정보가 조회되야 한다.")
    @Test
    void getStations() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 3);
        line.addSection(DEARIM_STATION, GANGNAM_STATION, 3);
        line.addSection(NOWON_STATION, SEOLLEUNG_STATION, 1);

        // when
        List<Station> stations = line.unmodifiableStations();

        // then
        assertThat(stations).hasSize(5);
        assertThat(stations).containsExactly(DEARIM_STATION, GANGNAM_STATION, NOWON_STATION, SEOLLEUNG_STATION, SUWON_STATION);
    }

    @DisplayName("지하철 노선에 등록된 구간을 조회하면 지금까지 등록된 모든 구간에 정보가 조회되야 한다.")
    @Test
    void getSections() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        line.addSection(DEARIM_STATION, GANGNAM_STATION, 7);
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 5);
        line.addSection(NOWON_STATION, SEOLLEUNG_STATION, 4);

        // when
        List<Section> sections = line.unmodifiableSections();

        // then
        assertThat(sections)
                .hasSize(4)
                .extracting("upStation", "downStation", "distance")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(DEARIM_STATION, GANGNAM_STATION, 7),
                        Tuple.tuple(GANGNAM_STATION, NOWON_STATION, 6),
                        Tuple.tuple(NOWON_STATION, SEOLLEUNG_STATION, 4),
                        Tuple.tuple(SEOLLEUNG_STATION, SUWON_STATION, 5)
                );
    }

    @DisplayName("지하철 노선에 구간을 삭제하면 노선 역이름 조회시 삭제한 역은 제외되야 한다.")
    @Test
    void removeSection() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);
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
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);

        // when then
        assertThatThrownBy(() -> line.removeSection(SEOLLEUNG_STATION))
                .isExactlyInstanceOf(SectionDeleteMinSizeException.class)
                .hasMessage("구간이 1개인 경우 삭제할 수 없습니다.");
    }

    @DisplayName("지하철 노선 추가 후 구간 삭제시 구간에 포함된 역이 아닌경우 삭제에 실패되어야 한다.")
    @Test
    void not_exist_station_removeSection_fail() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 8);

        // when then
        assertThatThrownBy(() -> line.removeSection(NOWON_STATION))
                .isExactlyInstanceOf(SectionNotFoundException.class)
                .hasMessage("구간정보를 찾을 수 없습니다.");
    }

    @DisplayName("지하철 노선 추가 및 삭제 후 지하철 노선에 등록된 구간을 조회하면 지금까지 등록된 모든 구간에 정보가 조회되야 한다.")
    @Test
    void removeSection_then_getSections() {
        // given
        Line line = new Line(SHINBUNDANG_LINE_NAME, SHINBUNDANG_LINE_COLOR, GANGNAM_STATION, SEOLLEUNG_STATION, 10);
        line.addSection(SEOLLEUNG_STATION, SUWON_STATION, 8);
        line.addSection(NOWON_STATION, SUWON_STATION, 4);
        line.removeSection(NOWON_STATION);
        // when
        List<Section> sections = line.unmodifiableSections();

        // then
        assertThat(sections)
                .hasSize(2)
                .extracting("upStation", "downStation", "distance")
                .containsExactlyInAnyOrder(
                        Tuple.tuple(GANGNAM_STATION, SEOLLEUNG_STATION, 10),
                        Tuple.tuple(SEOLLEUNG_STATION, SUWON_STATION, 8)
                );
    }
}
