package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;
    private Station 광나루역;
    private Station 아차산역;
    private Station 군자역;

    @BeforeEach
    void setup() {
        군자역 = new Station(1L, "군자역");
        아차산역 = new Station(2L, "아차산역");
        광나루역 = new Station(3L, "광나루역");
        line = new Line("5호선", "보라색");
        line.getSections().add(Section.of(line, 군자역, 아차산역, 10));
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우 마지막 역은 구간의 하행역이다")
    @Test
    void addSection() {
        // given
        Section section = Section.of(line, 아차산역, 광나루역, 10);

        // when
        line.addSection(section);

        // then
        List<Section> sections = line.getSections();
        Section lastSection = sections.get(sections.size() - 1);
        assertThat(lastSection.getDownStation()).isEqualTo(광나루역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Section section = Section.of(line, 아차산역, 광나루역, 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getStations()).containsExactly(군자역, 아차산역, 광나루역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Section section = Section.of(line, 아차산역, 광나루역, 10);
        line.addSection(section);

        // when
        line.removeSection(광나루역);

        // then
        assertThat(line.getStations()).containsExactly(군자역, 아차산역);
    }
}
