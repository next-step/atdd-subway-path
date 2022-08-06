package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.service.LineUpdateDto;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        이호선.update(new LineUpdateDto("신분당선", "red"));
        
        // then
        assertThat(이호선.getName()).isEqualTo("신분당선");
        assertThat(이호선.getColor()).isEqualTo("red");
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // Given
        이호선에_강남역_역삼역_구간_추가();

        // When
        이호선에_역삼역_삼성역_구간_추가();

        // Then
        final List<Section> 이호선_구간_리스트 = 이호선.getSections();
        assertThat(이호선_구간_리스트).hasSize(2);
    }

    @DisplayName("구간 목록 가져오기")
    @Test
    void getStations() {
        // Given
        이호선에_강남역_역삼역_구간_추가();

        // When
        final List<Section> 이호선_구간_리스트 = 이호선.getSections();

        // Then
        assertThat(이호선_구간_리스트).hasSize(1);
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        // Given
        final Section 강남역_역삼역_구간 = 이호선에_강남역_역삼역_구간_추가();

        // When
        이호선에서_강남역_역삼역_구간_삭제(강남역_역삼역_구간);

        // Then
        final List<Section> 이호선_구간_리스트 = 이호선.getSections();
        assertThat(이호선_구간_리스트).hasSize(0);
    }

    private Section 이호선에_강남역_역삼역_구간_추가() {
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);
        return 강남역_역삼역_구간;
    }

    private void 이호선에_역삼역_삼성역_구간_추가() {
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(역삼역_삼성역_구간);
    }

    private void 이호선에서_강남역_역삼역_구간_삭제(Section 강남역_역삼역_구간) {
        이호선.removeSection(강남역_역삼역_구간);
    }
}
