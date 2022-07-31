package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.domain.fixture.StationFixture.*;
import static nextstep.subway.domain.fixture.StationFixture.NAMBU_BUS_TERMINAL_STATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LinesTest {

    @Test
    @DisplayName("지하철 역 조회 시 모든 노선 지하철 역을 중복제거해서 리턴한다.")
    void getStations() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(SEOUL_UNIV_EDUCATION_STATION, GANGNAM_STATION, 10);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION, 10);

        // when
        Lines allLines = new Lines(List.of(greenLine, orangeLine));

        // then
        assertThat(allLines.getStations()).containsOnly(SEOUL_UNIV_EDUCATION_STATION,
                GANGNAM_STATION,
                NAMBU_BUS_TERMINAL_STATION);
    }

    @Test
    @DisplayName("지하철 역 조회 시 모든 노선의 구간을 리턴한다.")
    void getSections() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(SEOUL_UNIV_EDUCATION_STATION, GANGNAM_STATION, 10);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION, 10);

        // when
        Lines allLines = new Lines(List.of(greenLine, orangeLine));

        // then
        assertThat(allLines.getSections().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("노선 목록에 포함되어 있지 않는 지하철 역을 찾으면 예외를 반환한다.")
    void findStation_not_found() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(SEOUL_UNIV_EDUCATION_STATION, GANGNAM_STATION, 10);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION, 10);

        // when
        Lines allLines = new Lines(List.of(greenLine, orangeLine));

        // then
        assertThatThrownBy(() -> allLines.findStation(YEOKSAM_STATION.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선 목록에 지하철역을 찾으면 해당하는 지하철역을 반환한다.")
    void findStation() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(SEOUL_UNIV_EDUCATION_STATION, GANGNAM_STATION, 10);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION, 10);

        // when
        Lines allLines = new Lines(List.of(greenLine, orangeLine));

        // then
        assertAll(() -> {
            assertThat(allLines.findStation(SEOUL_UNIV_EDUCATION_STATION.getId())).isEqualTo(SEOUL_UNIV_EDUCATION_STATION);
            assertThat(allLines.findStation(GANGNAM_STATION.getId())).isEqualTo(GANGNAM_STATION);
            assertThat(allLines.findStation(NAMBU_BUS_TERMINAL_STATION.getId())).isEqualTo(NAMBU_BUS_TERMINAL_STATION);
        });
    }
}