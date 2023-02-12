package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayRestApiException;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.AddTypeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    @Test
    void addSection() {
        Line line = 지하철노선_생성_기존구간_추가("지하철역1","지하철역3",5);

        지하철노선_구간_지하철역_검증(line, "지하철역1", "지하철역3");
    }

    private ListAssert<String> 지하철노선_구간_지하철역_검증(Line line, String... stationNames) {
        return assertThat(line.getStations().stream().map(Station::getName)).containsExactly(stationNames);
    }

    @Test
    void addSection_front() {
        Line line = 지하철노선_생성_기존구간_추가("지하철역1", "지하철역3", 5);

        Section newSection = createSection(line, "지하철역0", "지하철역1", 5);
        line.addSection(FRONT_ADD_SECTION, newSection);

        지하철노선_구간_지하철역_검증(line, "지하철역0", "지하철역1", "지하철역3");
    }

    @DisplayName("지하철 구간 중간에 새로운 구간을 추가")
    @Test
    void addSection_middle() {
        Line line = 지하철노선_생성_기존구간_추가("지하철역1", "지하철역3", 7);

        Section newSection = createSection(line, "지하철역1", "지하철역2", 5);
        line.addSection(MIDDLE_ADD_SECTION, newSection);

        지하철노선_구간_지하철역_검증(line, "지하철역1", "지하철역2", "지하철역3");
    }

    @DisplayName("지하철 구간 중간에 새로운 구간을 추가 중 구간길이 검증 실패로 Exception 발생")
    @Test
    void addSection_middle_Exception1() {
        Line line = 지하철노선_생성_기존구간_추가("지하철역1", "지하철역3", 7);

        Section newSection = createSection(line, "지하철역1", "지하철역2", 7);

        assertThrows(SubwayRestApiException.class, () -> line.addSection(MIDDLE_ADD_SECTION, newSection));
    }

    @DisplayName("지하철 구간 중간에 새로운 구간 지하철역이 모두 존재하는 경우 Exception 발생")
    @Test
    void addSection_middle_Exception2() {
        Line line = 지하철노선_생성_기존구간_추가("지하철역1", "지하철역3", 7);

        Section newSection = new Section(line, new Station("지하철역1"), new Station("지하철역3"), 5);

        assertThrows(SubwayRestApiException.class, () -> line.addSection(MIDDLE_ADD_SECTION, newSection));
    }

    @DisplayName("지하철 구간 중간에 새로운 구간 지하철역이 노선에 아예 없는 경우 Exception 발생")
    @Test
    void addSection_middle_Exception3() {
        Line line = 지하철노선_생성_기존구간_추가("지하철역1", "지하철역3", 7);

        Section newSection = new Section(line, new Station("지하철역4"), new Station("지하철역5"), 5);

        assertThrows(SubwayRestApiException.class, () -> line.addSection(MIDDLE_ADD_SECTION, newSection));
    }

    @Test
    void getStations() {
        Station station = new Station("지하철역1");
        Station station2 = new Station("지하철역2");
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = new Section(line, station, station2, 5);
        line.addSection(BACK_ADD_SECTION, section);

        지하철노선_구간_지하철역_검증(line, "지하철역1", "지하철역2");
    }

    @Test
    void removeSection() {
        Station station = new Station("지하철역1");
        Station station2 = new Station("지하철역2");
        Station station3 = new Station("지하철역3");
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = new Section(line, station, station2, 5);
        Section section2 = new Section(line, station2, station3, 10);
        line.addSection(BACK_ADD_SECTION, section);
        line.addSection(BACK_ADD_SECTION, section2);

        line.removeSection(station3);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(station3.getName()))).isEqualTo(false);
    }

    private Section createSection(Line line, String upStationName, String downStationName, int distance) {
        return new Section(line, new Station(upStationName), new Station(downStationName), distance);
    }

    private Line 지하철노선_생성_기존구간_추가(String upStationName, String downStationName, int distance) {
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = createSection(line, upStationName, downStationName, distance);
        line.addSection(BACK_ADD_SECTION, section);

        return line;
    }
}
