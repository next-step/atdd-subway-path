package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineTest {

    Station 강남구청역;
    Station 압구정로데오역;
    Line 수인분당선;
    final int DISTANCE = 10;

    @BeforeEach
    void setUp() {
        강남구청역 = new Station("강남구청역");
        압구정로데오역 = new Station("압구정로데오역");
        수인분당선 = new Line("수인분당선", "bg-yellow-600");
    }

    @Test
    void addSection() {
        수인분당선.addSection(강남구청역, 압구정로데오역, DISTANCE);

        List<Section> sections = 수인분당선.getSections();
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(강남구청역.getId());
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(압구정로데오역.getId());
        assertThat(sections.get(0).getDistance()).isEqualTo(DISTANCE);
    }

    @Test
    void getStations() {
        수인분당선.addSection(강남구청역, 압구정로데오역, DISTANCE);

        List<Station> stations = 수인분당선.getStations();

        assertThat(stations.get(0).getId()).isEqualTo(강남구청역.getId());
        assertThat(stations.get(1).getId()).isEqualTo(압구정로데오역.getId());
    }

    @Test
    void removeSection() {
        수인분당선.addSection(강남구청역, 압구정로데오역, DISTANCE);

        수인분당선.removeSection(압구정로데오역);

        List<Section> sections = 수인분당선.getSections();
        assertThat(sections).isEmpty();
    }
}
