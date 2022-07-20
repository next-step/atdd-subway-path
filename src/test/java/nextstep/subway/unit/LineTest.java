package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련")
class LineTest {

    @Test
    @DisplayName("지하철 구간 추가")
    void addSection() {
        // given
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);

        이호선.addSection(강남역_역삼역_구간);

        assertThat(이호선.getSectionList()).containsExactly(강남역_역삼역_구간);
    }

    @Test
    @DisplayName("지하철 구간 정보들을 조회한다.")
    void getStations() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 역삼역_선릉역_구간 = new Section(이호선, 역삼역, 선릉역, 4);

        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_선릉역_구간);

        assertThat(이호선.getSectionList()).hasSize(2);
    }

    @Test
    @DisplayName("지하철 구간 삭제")
    void removeSection() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        이호선.addSection(강남역_역삼역_구간);

        이호선.removeStation(역삼역);

        assertThat(이호선.isEmptySections()).isTrue();
    }

    @Test
    @DisplayName("지하철 구간 삭제 - 하행선이 아닐때 에러")
    void removeSectionException() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        이호선.addSection(강남역_역삼역_구간);

        assertThatIllegalArgumentException().isThrownBy(() ->
            이호선.removeStation(강남역)
        );
    }


    @Test
    @DisplayName("지하철 노선 업데이트")
    void updateLine() {
        Line 이호선 = new Line("2호선", "bg-green-600");

        이호선.update("신분당선", "bg-red-300");

        assertAll(() -> {
            assertThat(이호선.getName()).isEqualTo("신분당선");
            assertThat(이호선.getColor()).isEqualTo("bg-red-300");
        });
    }
}
