package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayRestApiException;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.AddTypeEnum.BACK_ADD_SECTION;
import static nextstep.subway.common.AddTypeEnum.FRONT_ADD_SECTION;
import static nextstep.subway.common.AddTypeEnum.MIDDLE_ADD_SECTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    Station 지하철역0 = new Station("지하철역0");
    Station 지하철역1 = new Station("지하철역1");
    Station 지하철역2 = new Station("지하철역2");
    Station 지하철역3 = new Station("지하철역3");
    int distance_5 = 5;
    int distance_7 = 7;
    int distance_10 = 10;

    @Test
    void addSection() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_5);

        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역3);
    }

    @Test
    void addSection_front() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_5);

        Section newSection = createSection(line, 지하철역0, 지하철역1, distance_5);
        line.addSection(FRONT_ADD_SECTION, newSection);

        지하철노선_구간_지하철역_검증(line, 지하철역0, 지하철역1, 지하철역3);
    }

    @DisplayName("지하철 구간 중간에 새로운 구간을 추가")
    @Test
    void addSection_middle() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);

        Section newSection = createSection(line, 지하철역1, 지하철역2, distance_5);
        line.addSection(MIDDLE_ADD_SECTION, newSection);

        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역2, 지하철역3);
    }

    @DisplayName("지하철 구간 중간에 새로운 구간을 추가 중 구간길이 검증 실패로 Exception 발생")
    @Test
    void addSection_middle_Exception1() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);

        Section newSection = createSection(line, 지하철역1, 지하철역2, distance_7);

        assertThrows(SubwayRestApiException.class, () -> line.addSection(MIDDLE_ADD_SECTION, newSection));
    }

    @DisplayName("지하철 구간 중간에 새로운 구간 지하철역이 모두 존재하는 경우 Exception 발생")
    @Test
    void addSection_middle_Exception2() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);
        Section newSection = createSection(line, 지하철역1, 지하철역3, distance_5);

        assertThrows(SubwayRestApiException.class, () -> line.addSection(MIDDLE_ADD_SECTION, newSection));
    }

    @DisplayName("지하철 구간 중간에 새로운 구간 지하철역이 노선에 아예 없는 경우 Exception 발생")
    @Test
    void addSection_middle_Exception3() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);
        Section newSection = createSection(line, new Station("지하철역4"), new Station("지하철역5"), distance_5);

        assertThrows(SubwayRestApiException.class, () -> line.addSection(MIDDLE_ADD_SECTION, newSection));
    }

    @Test
    void removeSection() {
        Line line = 지하철노선_생성_기존구간_추가_2();

        line.removeSection(지하철역2);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(지하철역2.getName()))).isEqualTo(false);
        지하철노선_구간_지하철역_검증(line, 지하철역0, 지하철역1, 지하철역3);
    }

    @Test
    void removeSection2() {
        Line line = 지하철노선_생성_기존구간_추가_2();

        line.removeSection(지하철역0);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(지하철역0.getName()))).isEqualTo(false);
        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역2, 지하철역3);
    }

    @Test
    void removeSection3() {
        Line line = 지하철노선_생성_기존구간_추가_2();

        line.removeSection(지하철역3);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(지하철역3.getName()))).isEqualTo(false);
        지하철노선_구간_지하철역_검증(line, 지하철역0, 지하철역1, 지하철역2);
    }

    @Test
    void getStations() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section newSection = Section.builder()
                .line(line)
                .upStation(지하철역1)
                .downStation(지하철역2)
                .distance(distance_5)
                .build();

        line.addSection(BACK_ADD_SECTION, newSection);

        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역2);
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    private Line 지하철노선_생성_기존구간_추가(Station upStation, Station downStation, int distance) {
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = createSection(line, upStation, downStation, distance);
        line.addSection(BACK_ADD_SECTION, section);

        return line;
    }

    private Line 지하철노선_생성_기존구간_추가_2() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = createSection(line, 지하철역0, 지하철역1, distance_5);
        Section section2 = createSection(line, 지하철역1, 지하철역2, distance_10);
        Section section3 = createSection(line, 지하철역2, 지하철역3, distance_7);

        line.addSection(BACK_ADD_SECTION, section);
        line.addSection(BACK_ADD_SECTION, section2);
        line.addSection(BACK_ADD_SECTION, section3);

        return line;
    }

    private ListAssert<Station> 지하철노선_구간_지하철역_검증(Line line, Station... stations) {
        return assertThat(line.getStations()).containsExactly(stations);
    }
}
