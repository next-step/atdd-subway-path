package nextstep.subway.application.strategy.addition;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Station;

class AddSectionAfterLastDownStationStrategyTest {
    private final AddSectionAfterLastDownStationStrategy strategy = new AddSectionAfterLastDownStationStrategy();

    @Nested
    @DisplayName("구간 추가 가능 여부 확인")
    class CanAddTests {
        @Test
        @DisplayName("마지막 구간의 하행역과 새 구간의 상행역이 같고 구간이 다를 때 추가 가능하다")
        void canAddTrue() {
            // given

            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Section existingSection = new Section(null, null, gangnamStation, yeoksamStation, 10);
            Section newSection = new Section(null, null, yeoksamStation, new Station(3L, "선릉역"), 8);

            Line line = new Line("2호선", "green");
            line.addSection(existingSection);

            // when
            boolean result = strategy.canAddToExistingSection(line.getSections(), existingSection, newSection);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("구간 추가")
    class AddSectionTests {

        @Test
        @DisplayName("기존 구간의 마지막에 새 구간을 추가하면 구간이 추가된다")
        void addSectionSuccess() {
            // given
            Line line = new Line("2호선", "green");
            Station gangnamStation = new Station(1L, "강남역");
            Station yeoksamStation = new Station(2L, "역삼역");
            Station seolleungStation = new Station(3L, "선릉역");

            Section newSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 10);

            line.addSection(newSection);


            // when
            strategy.addSection(line, additionalSection);

            // then
            assertThat(line.getUnmodifiableSections()).contains(newSection);
        }
    }
}