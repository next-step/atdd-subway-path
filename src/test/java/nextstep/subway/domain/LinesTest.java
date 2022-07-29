package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.domain.fixture.StationFixture.*;
import static nextstep.subway.domain.fixture.StationFixture.NAMBU_BUS_TERMINAL_STATION;
import static org.assertj.core.api.Assertions.assertThat;

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
}