package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CannotDeleteSoleSectionException;
import nextstep.subway.exception.InvalidSectionDistanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
        // given
        Section section = new Section(분당선, 수서역, 복정역, 5);

        // when
        분당선.addSection(section);

        // then
        assertThat(분당선.getSections()).containsExactly(section);
    }

    @DisplayName("지하철 구간 등록 시, 구간의 길이는 최소 1 이상이어야 한다.")
    @ValueSource(ints = {-1, 0})
    @ParameterizedTest
    void invalidDistance(int distance) {
        // when & then
        assertThatThrownBy(() -> 분당선.addSection(new Section(분당선, 수서역, 복정역, distance)))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("지하철 노선의 마지막 구간을 제거한다.")
    @Test
    void removeLastLineSection() {
        // given
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));
        분당선.addSection(new Section(분당선, 복정역, 가천대역, 5));

        // when
        분당선.removeSection(가천대역);

        // then
        assertThat(분당선.getStations()).containsExactly(수서역, 복정역);
    }

    @DisplayName("지하철 노선의 중간 구간을 제거한다.")
    @Test
    void removeIntermediateLineSection() {
        // given
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));
        분당선.addSection(new Section(분당선, 복정역, 가천대역, 5));

        // when
        분당선.removeSection(복정역);

        // then
        assertThat(분당선.getStations()).containsExactly(수서역, 가천대역);
    }

    @DisplayName("지하철 구간 제거 시, 노선에 등록된 구간이 하나라면 예외가 발생한다.")
    @Test
    void cannotDeleteSectionWhenSingleSection() {
        // given
        분당선.addSection(new Section(분당선, 수서역, 복정역, 5));

        // when & then
        assertThatThrownBy(() -> 분당선.removeSection(가천대역))
            .isInstanceOf(CannotDeleteSoleSectionException.class);
    }
}
