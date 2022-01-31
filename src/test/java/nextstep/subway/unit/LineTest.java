package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        Line line = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section section = new Section(line, 강남역, 역삼역, 10);
        line.addSection(section);

        assertThat(line.getSections()).hasSize(1);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        Line line = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section section = new Section(line, 강남역, 역삼역, 10);
        line.addSection(section);

        List<Station> stations = line.getAllStations();

        assertThat(stations).hasSize(2);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        Line line = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 10);
        line.addSection(section1);
        line.addSection(section2);

        line.removeSection(선릉역);

        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("라인의 이름과 색상 변경")
    void updateLine() {
        Line line = new Line("2호선", "bg-green-600");

        line.update("3호선", "bg-orange-600");

        assertThat(line.getName()).isEqualTo("3호선");
        assertThat(line.getColor()).isEqualTo("bg-orange-600");
    }
}
