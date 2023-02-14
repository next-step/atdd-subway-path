package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.DuplicateAddSectionException;
import nextstep.subway.domain.exception.IllegalAddSectionException;
import nextstep.subway.domain.exception.IllegalDistanceSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class LineTest {

    private Line line;
    Station 양재역 = new Station("양재역");
    Station 강남역 = new Station("강남역");

    @BeforeEach
    void setUp() {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(line, 강남역, 양재역, 10));

        line = new Line("신분당선", "red", sections);
    }

    /**
     * Given 새로운 역을 추가 후
     * When 기존 구간 맨 앞에 새로운 구간을 추가 시
     * Then 추가가 된다.
     */
    @DisplayName("기존 구간 맨 앞에 구간 추가 시 구간이 추가가 된다")
    @Test
    void 기존_구간_맨_앞에_구간_추가_시_구간이_추가가_된다() {
        // Given
        Station 논현역 = new Station("논현역");

        // When
        line.addSection(논현역, 강남역, 7);

        // Then
        List<Section> sections = line.getSections();
        assertThat(sections.get(0).getUpStation()).isEqualTo(강남역);
        assertThat(sections.get(0).getDownStation()).isEqualTo(양재역);
        assertThat(sections.get(0).getDistance()).isEqualTo(10);
        assertThat(sections.get(1).getUpStation()).isEqualTo(논현역);
        assertThat(sections.get(1).getDownStation()).isEqualTo(강남역);
        assertThat(sections.get(1).getDistance()).isEqualTo(7);
    }

    /**
     * Given 새로운 역을 추가 후
     * When 기존 구간 중간에 새로운 구간 추가 시
     * Then 추가가 된다
     * Then 거리가 재조정이 된다
     */
    @DisplayName("기존 구간 중간에 새로운 구간을 추가 시 구간이 추가가 된다")
    @Test
    void 기존_구간_중간에_새로운_구간을_추가_시_구간이_추가가_된다() {
        // Given
        Station 새로운역 = new Station("새로운역");

        // When
        line.addSection(강남역, 새로운역, 4);

        // Then
        List<Section> sections = line.getSections();
        assertThat(sections.get(0).getUpStation()).isEqualTo(새로운역);
        assertThat(sections.get(0).getDownStation()).isEqualTo(양재역);
        assertThat(sections.get(0).getDistance()).isEqualTo(6);
        assertThat(sections.get(1).getUpStation()).isEqualTo(강남역);
        assertThat(sections.get(1).getDownStation()).isEqualTo(새로운역);
        assertThat(sections.get(1).getDistance()).isEqualTo(4);
    }

    /**
     * Given 새로운 역을 추가 후
     * When 기존 구간 맨 뒤에 새로운 구간 추가 시
     * Then 추가가 된다
     */
    @DisplayName("기존 구간 맨 뒤에 새로운 구간을 추가 시 구간이 추가가 된다")
    @Test
    void 기존_구간_맨_뒤에_새로운_구간을_추가_시_구간이_추가가_된다() {
        // Given
        Station 판교역 = new Station("판교역");

        // When
        line.addSection(양재역, 판교역, 4);

        // Then
        List<Section> sections = line.getSections();
        assertThat(sections.get(0).getUpStation()).isEqualTo(강남역);
        assertThat(sections.get(0).getDownStation()).isEqualTo(양재역);
        assertThat(sections.get(0).getDistance()).isEqualTo(10);
        assertThat(sections.get(1).getUpStation()).isEqualTo(양재역);
        assertThat(sections.get(1).getDownStation()).isEqualTo(판교역);
        assertThat(sections.get(1).getDistance()).isEqualTo(4);
    }

    /**
     * When 기존 구간과 동일한 구간 추가 요청시
     * Then 추가가 안된다
     */
    @DisplayName("기존 구간과 동일한 구간 추가 요청시 추가가 안된다")
    @Test
    void 기존_구간과_동일한_구간_추가_요청시_추가가_안된다() {
        // When && Then
        assertThatThrownBy(() -> line.addSection(강남역, 양재역, 4))
            .isInstanceOf(DuplicateAddSectionException.class);
    }

    /**
     * When 기존 구간에 기존 거리보다 새로운 구간의 거리가 같거나 더 클 시
     * Then 추가가 안된다
     */
    @DisplayName("기존 구간에 기존 거리보다 새로운 구간의 거리가 같거나 더 클 시 추가가 안된다")
    @Test
    void 기존_구간에_기존_거리보다_새로운_구간의_거리가_더_클_시_추가가_안된다() {
        // Given
        Station 판교역 = new Station("판교역");

        // When && Then
        assertThatThrownBy(() -> line.addSection(강남역, 판교역, 10))
            .isInstanceOf(IllegalDistanceSectionException.class);
    }

    /**
     * When 기존 구간에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 추가가 안된다
     */
    @DisplayName("기존 구간에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가가 안된다")
    @Test
    void 기존_구간에_상행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가가_안된다() {
        // Given
        Station 판교역 = new Station("판교역");
        Station 수지구청역 = new Station("수지구청역");

        // When && Then
        assertThatThrownBy(() -> line.addSection(수지구청역, 판교역, 10))
            .isInstanceOf(IllegalAddSectionException.class);
    }

    /**
     * When 기존 구간들에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간 요청 시
     * Then 추가가 안된다
     */
    @DisplayName("기존 구간들에 상행역과 하행역 둘 중 하나도 포함되어있지 않은 구간 요청 시 추가가 안된다")
    @Test
    void 기존_구간들에_상행역과_하행역_둘_중_하나도_포함되어있지_않은_구간_요청_시_추가가_안된다() {
        // Given
        Station 수지구청역 = new Station("수지구청역");
        Station 판교역 = new Station("판교역");

        // When && Then
        assertThatThrownBy(() -> line.addSection(수지구청역, 판교역, 10))
            .isInstanceOf(IllegalAddSectionException.class);
    }

    // When 라인에 해당하는 역들을 요청 시
    // Then 상행역 부터 역들이 조회가 된다
    @DisplayName("라인에 해당하는 역들을 요청 시 상행역 부터 역들이 조회가 된다")
    @Test
    void 라인에_해당하는_역들을_요청_시_상행역_부터_역들이_조회가_된다() {
        // When
        List<Station> stations = line.getStations();

        // Then
        assertThat(stations).containsExactly(강남역, 양재역);
    }

    @Test
    void removeSection() {
    }

}
