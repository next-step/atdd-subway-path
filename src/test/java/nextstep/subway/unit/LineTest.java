package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SubwayRestApiException;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    Station 지하철역0 = new Station("지하철역0");
    Station 지하철역1 = new Station("지하철역1");
    Station 지하철역2 = new Station("지하철역2");
    Station 지하철역3 = new Station("지하철역3");
    int distance_5 = 5;
    int distance_7 = 7;

    @Test
    void addSection() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_5);

        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역3);
    }

    @Test
    void addSectionFront() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_5);

        Section newSection = createSection(line, 지하철역0, 지하철역1, distance_5);
        지하철노선_기존구간_앞에_추가(line, newSection);

        지하철노선_구간_지하철역_검증(line, 지하철역0, 지하철역1, 지하철역3);
    }

    @DisplayName("지하철 구간 중간에 새로운 구간을 추가")
    @Test
    void addSectionMiddle() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);

        Section newSection = createSection(line, 지하철역1, 지하철역2, distance_5);
        지하철노선_기존구간_중간에_추가(line, newSection);

        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역2, 지하철역3);
    }

    @DisplayName("지하철 구간 중간에 새로운 구간을 추가 중 구간길이 검증 실패로 Exception 발생")
    @Test
    void addSectionMiddleException1() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);

        Section newSection = createSection(line, 지하철역1, 지하철역2, distance_7);

        assertThrows(SubwayRestApiException.class, () -> 지하철노선_기존구간_중간에_추가(line, newSection));
    }

    @DisplayName("지하철 구간 중간에 새로운 구간 지하철역이 모두 존재하는 경우 Exception 발생")
    @Test
    void addSectionMiddleException2() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);
        Section newSection = createSection(line, 지하철역1, 지하철역3, distance_5);

        assertThrows(SubwayRestApiException.class, () -> 지하철노선_기존구간_중간에_추가(line, newSection));
    }

    @DisplayName("지하철 구간 중간에 새로운 구간 지하철역이 노선에 아예 없는 경우 Exception 발생")
    @Test
    void addSectionMiddleException3() {
        Line line = 지하철노선_생성_기존구간_추가(지하철역1, 지하철역3, distance_7);
        Section newSection = createSection(line, new Station("지하철역4"), new Station("지하철역5"), distance_5);

        assertThrows(SubwayRestApiException.class, () -> 지하철노선_기존구간_중간에_추가(line, newSection));
    }

    @Test
    void removeSectionFirstStation() {
        Line line = 지하철노선_생성_기존구간_추가_2(distance_5, 지하철역0, 지하철역1, 지하철역2, 지하철역3);

        line.removeSection(지하철역2);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(지하철역2.getName()))).isEqualTo(false);
        지하철노선_구간_지하철역_검증(line, 지하철역0, 지하철역1, 지하철역3);
    }

    @Test
    void removeSectionMiddleStation() {
        Line line = 지하철노선_생성_기존구간_추가_2(distance_5, 지하철역0, 지하철역1, 지하철역2, 지하철역3);

        line.removeSection(지하철역0);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(지하철역0.getName()))).isEqualTo(false);
        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역2, 지하철역3);
    }

    @Test
    void removeSectionLastStation() {
        Line line = 지하철노선_생성_기존구간_추가_2(distance_5, 지하철역0, 지하철역1, 지하철역2, 지하철역3);

        line.removeSection(지하철역3);

        assertThat(line.getStations().stream().anyMatch(a -> a.getName().equals(지하철역3.getName()))).isEqualTo(false);
        지하철노선_구간_지하철역_검증(line, 지하철역0, 지하철역1, 지하철역2);
    }

    @DisplayName("지하철 노선에 없는 지하철역을 삭제할 경우 Exception 발생")
    @Test
    void removeSectionException() {
        Line line = 지하철노선_생성_기존구간_추가_2(distance_5, 지하철역0, 지하철역1, 지하철역2, 지하철역3);

        assertThrows(SubwayRestApiException.class, () -> line.removeSection(new Station("지하철역4")));
    }

    @DisplayName("지하철 노선의 구간이 1개인 상태에서 지하철역을 삭제할 경우 Exception 발생")
    @Test
    void removeSectionException2() {
        Line line = 지하철노선_생성_기존구간_추가_2(distance_5, 지하철역0, 지하철역1);

        assertAll(() -> assertThrows(SubwayRestApiException.class, () -> line.removeSection(지하철역0)),
                  () -> assertThrows(SubwayRestApiException.class, () -> line.removeSection(지하철역1)));
    }

    @Test
    void getStations() {
        Line line = new Line("지하철노선", "bg-red-600");
        Section newSection = Section.builder()
                .line(line)
                .upStation(지하철역1)
                .downStation(지하철역2)
                .distance(new Distance(distance_5))
                .build();

        지하철노선_기존구간_뒤에_추가(line, newSection);

        지하철노선_구간_지하철역_검증(line, 지하철역1, 지하철역2);
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(distance))
                .build();
    }

    private Line 지하철노선_생성_기존구간_추가(Station upStation, Station downStation, int distance) {
        Line line = new Line("지하철노선", "bg-red-600");
        Section section = createSection(line, upStation, downStation, distance);

        지하철노선_기존구간_뒤에_추가(line, section);

        return line;
    }

    private Line 지하철노선_생성_기존구간_추가_2(int distance, Station... station) {
        Line line = new Line("지하철노선", "bg-red-600");

        for (int i = 0; i < station.length-1; i++) {
            지하철구간_추가(line, station[i], station[i+1], distance);
        }

        return line;
    }

    private void 지하철구간_추가(Line line, Station upStation, Station downStation, int distance) {
        Section section = createSection(line, upStation, downStation, distance);
        지하철노선_기존구간_뒤에_추가(line, section);
    }

    private void 지하철노선_기존구간_앞에_추가(Line line, Section newSection) {
        line.addSection(newSection);
    }

    private void 지하철노선_기존구간_중간에_추가(Line line, Section newSection) {
        line.addSection(newSection);
    }

    private void 지하철노선_기존구간_뒤에_추가(Line line, Section newSection) {
        line.addSection(newSection);
    }

    private ListAssert<Station> 지하철노선_구간_지하철역_검증(Line line, Station... stations) {
        return assertThat(line.getStations()).containsExactly(stations);
    }
}
