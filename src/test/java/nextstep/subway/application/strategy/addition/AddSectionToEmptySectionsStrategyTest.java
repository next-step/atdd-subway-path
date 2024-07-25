package nextstep.subway.application.strategy.addition;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;

class AddSectionToEmptySectionsStrategyTest {
    private final AddSectionToEmptySectionsStrategy strategy = new AddSectionToEmptySectionsStrategy();

    @Nested
    @DisplayName("구간 추가 가능 여부 확인")
    class CanAddTests {

        @Test
        @DisplayName("빈 구간 리스트에 새로운 구간을 추가할 수 있다")
        void canAddTrue() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section newSection = new Section(line, gangnamStation, yeoksamStation, 10);

            // when
            boolean result = strategy.canApply(line, newSection);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("구간 리스트가 비어 있지 않으면 새로운 구간을 추가할 수 없다")
        void canAddFalse() {
            // given
            Line line = new Line("2호선", "green");

            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station samsungStation = new Station(3L, "삼성역");

            Section existingSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(existingSection);

            Section newSection = new Section(line, yeoksamStation, samsungStation, 8);

            // when
            boolean result = strategy.canApply(line, newSection);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("구간 추가")
    class AddSectionTests {

        @Test
        @DisplayName("빈 구간 리스트에 새로운 구간을 추가하면 구간이 추가된다")
        void addSectionSuccess() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section newSection = new Section(line, gangnamStation, yeoksamStation, 10);

            // when
            strategy.addSection(line, newSection);

            // then
            assertThat(line.getUnmodifiableSections()).containsExactly(newSection);
        }
    }
}
