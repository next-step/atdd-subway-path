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
        // given
        Line line = new Line("2호선", "green");

        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 홍대역 = new Station("홍대역");

        Section 기존_구간 = new Section(line, 강남역, 역삼역, 5);
        line.addSection(기존_구간);

        // when
        Section 새로운_구간 = new Section(line, 역삼역, 홍대역, 3);
        line.addSection(새로운_구간);

        // then
        assertThat(line.getSections().get(1)).isEqualTo(새로운_구간);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = new Line("2호선", "green");

        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 홍대역 = new Station("홍대역");

        Section 기존_구간 = new Section(line, 강남역, 역삼역, 5);
        line.addSection(기존_구간);
        Section 새로운_구간 = new Section(line, 역삼역, 홍대역, 3);
        line.addSection(새로운_구간);

        // when
        List<Section> sections = line.getSections();

        // then
        assertThat(sections).isEqualTo(List.of(기존_구간, 새로운_구간));
    }

    @DisplayName("구간 목록에서 마지막 역 삭제 = 마지막 구간 제거")
    @Test
    void removeSection() {
        // given
        Line line = new Line("2호선", "green");
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 홍대역 = new Station(3L, "홍대역");
        Section 첫번째_구간 = new Section(1L, line, 강남역, 역삼역, 3);
        Section 두번째_구간 = new Section(2L, line, 역삼역, 홍대역, 5);
        line.addSection(첫번째_구간);
        line.addSection(두번째_구간);

        // when
        line.deleteSection(3L);

        // then
        List<Section> 삭제후_구간_목록 = line.getSections();
        assertThat(삭제후_구간_목록.get(삭제후_구간_목록.size() - 1)).isEqualTo(첫번째_구간);
    }
}
