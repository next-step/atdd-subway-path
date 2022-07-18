package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.ConstStation.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        Line 신분당선 = Line.of("신분당선", "bg-red-600");

        Section section = Section.of(강남역, 신논현역, 10);

        신분당선.addSection(section);

        List<Section> sections = 신분당선.getSections();

        assertAll(
                () -> assertThat(sections).hasSize(1),
                () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                () -> assertThat(sections.get(0).getDownStation()).isEqualTo(신논현역)
        );
    }

    @DisplayName("지하철 노선에 존재하는 모든 역 조회")
    @Test
    void getStations() {
    }

    @DisplayName("지하철 노선에 특정 구간 제거")
    @Test
    void removeSection() {
    }
}
