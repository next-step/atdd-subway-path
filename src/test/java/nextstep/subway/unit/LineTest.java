package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private static final String YELLOW = "yellow";
    private Line 신분당선;
    private Station 미금역;
    private Station 판교역;
    private Station 강남역;

    /**
     * Given 지하철역과 노선을 생성하고
     */
    @BeforeEach
    public void setUp() {
        신분당선 = new Line("신분당선", "red");
        미금역 = new Station("미금역");
        판교역 = new Station("판교역");
        강남역 = new Station("강남역");
    }

    /**
     * When 지하철 노선에 새로운 구간을 추가하면
     * Then 노선에 새로운 구간이 추가된다.
     */
    @Test
    @DisplayName("지하철 구간을 추가한다.")
    void addSection() {
        // when
        Section section = new Section(신분당선, 미금역, 판교역, 10);
        신분당선.addSection(section);

        // then
        List<Section> sections = 신분당선.getSections();
        assertThat(sections).contains(section);
    }

    /**
     * Given 지하철 노선에 새로운 구간을 추가하고
     * When 지하철 노선의 모든 역을 조회하면
     * Then 모든 지하철 역이 조회된다.
     */
    @Test
    @DisplayName("노선의 모든 역을 조회한다.")
    void getStations() {
        // given
        Section section = new Section(신분당선, 미금역, 판교역, 10);
        신분당선.addSection(section);

        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations).containsExactly(미금역, 판교역);
    }

    /**
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // when
        LineRequest updateRequest = LineRequest.builder()
            .color(YELLOW)
            .build();
        신분당선.update(updateRequest);

        // then
        assertThat(신분당선.getColor()).isEqualTo(YELLOW);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @Test
    @DisplayName("지하철 구간을 제거한다.")
    void removeSection() {
        // given
        Section 미금역_판교역 = new Section(신분당선, 미금역, 판교역, 10);
        Section 판교역_강남역 = new Section(신분당선, 판교역, 강남역, 10);

        신분당선.addSection(미금역_판교역);
        신분당선.addSection(판교역_강남역);

        // when
        신분당선.deleteSection(강남역);

        // then
        List<Station> stations = 신분당선.getStations();
        assertThat(stations).containsExactly(미금역, 판교역);
    }
}
