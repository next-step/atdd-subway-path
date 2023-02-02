package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class LineTest {

    private Line 분당선;
    private Station 수서역;
    private Station 복정역;
    private Station 가천대역;

    @BeforeEach
    void setup() {
        분당선 = new Line("분당선", "yellow");
        수서역 = new Station("수서역");
        복정역 = new Station("복정역");
        가천대역 = new Station("가천대역");
    }

    @DisplayName("새로운 지하철 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        Section section = new Section(분당선, 수서역, 복정역, 5);
        분당선.addSection(section);

        // then
        assertThat(분당선.getSections()).containsExactly(section);
    }

    @DisplayName("지하철 노선에 등록된 역 목록을 조회한다.")
    @Test
    void getStations() {
        // when
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));

        // then
        assertThat(분당선.getStations()).containsExactlyInAnyOrder(수서역, 복정역);
    }

    @DisplayName("지하철 노선에 등록된 구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));
        분당선.addSection(new Section(분당선, 복정역, 가천대역, 5));

        // when
        분당선.removeSection(가천대역);

        // then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 구간 제거 시, 노선에 등록된 구간이 하나라면 예외가 발생한다.")
    @Test
    void cannotDeleteSectionWhenSingleSection() {
        // given
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));

        // when & then
        assertThatThrownBy(() -> 분당선.removeSection(가천대역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 제거 시, 전달한 역이 하행 종점역이 아니라면 예외가 발생한다.")
    @Test
    void cannotDeleteSectionWhenNonDownStation() {
        // given
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));
        분당선.addSection(new Section(분당선, 복정역, 가천대역, 5));

        // when & then
        assertThatThrownBy(() -> 분당선.removeSection(복정역))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
