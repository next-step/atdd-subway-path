package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void update() {
        // when
        이호선.update("신분당선", "red");

        // then
        assertThat(이호선.getName()).isEqualTo("신분당선");
        assertThat(이호선.getColor()).isEqualTo("red");
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(역삼역_삼성역_구간);

        // Then
        final Sections 이호선_구간_리스트 = 이호선.getSections();
        assertThat(이호선_구간_리스트.getList()).hasSize(2);
    }

    @DisplayName("구간 목록 가져오기")
    @Test
    void getStations() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        final Sections 이호선_구간_리스트 = 이호선.getSections();

        // Then
        assertThat(이호선_구간_리스트.getList()).hasSize(1);
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        이호선.removeSectionWithValidateStation(역삼역);

        // Then
        final Sections 이호선_구간_리스트 = 이호선.getSections();
        assertThat(이호선_구간_리스트.getList()).hasSize(0);
    }
}
