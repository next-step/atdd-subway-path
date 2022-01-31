package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class LineTest {

    @Test
    @DisplayName("라인의 이름과 색상 변경")
    void updateLine() {
        Line line = new Line("2호선", "bg-green-600");

        line.update("3호선", "bg-orange-600");

        assertThat(line.getName()).isEqualTo("3호선");
        assertThat(line.getColor()).isEqualTo("bg-orange-600");
    }

    @Test
    @DisplayName("라인에 Section 추가")
    void addSection() {
        Line line = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section section = new Section(line, 강남역, 역삼역, 10);
        line.addSection(section);

        assertThat(line.getSections()).hasSize(1);
    }
}