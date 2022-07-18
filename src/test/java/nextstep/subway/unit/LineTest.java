package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
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
        Line 신분당선 = Line.of("신분당선", "bg-red-600");

        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        List<Station> stations = 신분당선.allStations();

        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).containsExactly(강남역, 신논현역, 정자역)
        );
    }

    @DisplayName("지하철 노선에 특정 구간 제거")
    @Test
    void removeSection() {
        Line 신분당선 = Line.of("신분당선", "bg-red-600");

        Section 강남_신논현 = Section.of(강남역, 신논현역, 10);
        Section 신논현_정자 = Section.of(신논현역, 정자역, 5);

        신분당선.addSection(강남_신논현);
        신분당선.addSection(신논현_정자);

        신분당선.removeSection(정자역);

        List<Station> stations = 신분당선.allStations();

        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 신논현역)
        );
    }
}
