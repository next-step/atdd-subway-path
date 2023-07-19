package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 단위 테스트")
class LineTest {

    private final String COLOR_RED = "bg-red-600";

    private final String COLOR_BLUE = "bg-blue-600";
    private final int DISTANCE = 10;

    private Station 신사역, 광교역, 강남역;
    private Line 신분당선;
    @BeforeEach
    void set(){
        신사역 = new Station("신사역");
        광교역 = new Station("광교역");
        강남역 = new Station("강남역");
        신분당선 = new Line("신분당선", COLOR_RED, 신사역, 강남역, DISTANCE);
    }

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        Section newSection = new Section(신분당선, 강남역, 광교역, DISTANCE);
        신분당선.addSections(newSection);

        assertThat(신분당선.getStations()).contains(광교역);
    }

    @DisplayName("지하철 노선에서 역 목록 가져오기")
    @Test
    void getStations() {
        assertThat(신분당선.getStations()).contains(신사역,강남역);
    }

    @DisplayName("지하철 노선에서 구간 제거")
    @Test
    void removeSection() {
        Section newSection = new Section(신분당선, 강남역, 광교역, DISTANCE);
        신분당선.addSections(newSection);

        신분당선.removeSections(광교역);

        assertThat(신분당선.getStations()).doesNotContain(광교역);
    }
}
