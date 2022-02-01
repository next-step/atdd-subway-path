package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우 마지막 역은 구간의 하행역이다")
    @Test
    void addSection() {
        // given
        Station 군자역 = new Station("군자역");
        Station 아차산역 = new Station("아차산역");
        Station 광나루역 = new Station("광나루역");
        Line line = new Line("5호선", "보라색");
        line.getSections().add(new Section(line, 군자역, 아차산역, 10));

        Section section = Section.of(line, 아차산역, 광나루역, 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections()).hasSize(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
    }
}
