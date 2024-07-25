package nextstep.subway.unit;

import static nextstep.subway.domain.model.LineColor.*;
import static nextstep.subway.domain.model.LineName.*;
import static nextstep.subway.domain.model.Sections.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import nextstep.subway.application.DefaultSectionAdditionStrategyFactory;
import nextstep.subway.application.strategy.addition.AddSectionBeforeDownStationStrategy;
import nextstep.subway.application.strategy.addition.AddSectionAfterUpStationStrategy;
import nextstep.subway.application.strategy.addition.AddSectionAfterLastDownStationStrategy;
import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.model.Station;
import nextstep.subway.domain.service.SectionAdditionStrategy;
import nextstep.subway.domain.service.SectionAdditionStrategyFactory;

public class LineTest {
    private SectionAdditionStrategyFactory sectionAdditionStrategyFactory;

    @BeforeEach
    void setUp() {
        sectionAdditionStrategyFactory = new DefaultSectionAdditionStrategyFactory(
            List.of(
                new AddSectionAfterLastDownStationStrategy(),
                new AddSectionAfterUpStationStrategy(),
                new AddSectionBeforeDownStationStrategy()
            )
        );
    }

    @Nested
    @DisplayName("구간 추가 기능")
    class AddSection {

        @Test
        @DisplayName("이미 존재하는 구간을 추가하려고 하면 실패한다")
        void addSectionWithDuplicateSection() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(getStrategy(line, initialSection), initialSection))
                .withMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("새로운 구간을 추가할 때 해당 구간이 올바르면 구간이 추가된다")
        void addSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);

            // when
            line.addSection(initialSection);

            // then
            assertThat(line.getUnmodifiableSections()).contains(initialSection);
        }

        @Test
        @DisplayName("상행역과 하행역이 모두 기존 구간에 존재하면 추가를 실패한다")
        void addSectionWithBothExistingStations() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, seolleungStation, 10);
            line.addSection(initialSection);

            Section duplicateSection = new Section(line, gangnamStation, seolleungStation, 10);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(getStrategy(line, duplicateSection), duplicateSection))
                .withMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("상행역과 하행역이 모두 기존 구간과 다른 경우 추가를 실패한다")
        void addSectionWithBothNonExistingStations() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");
            Station samsungStation = new Station("삼성역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            Section invalidSection = new Section(line, seolleungStation, samsungStation, 5);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(getStrategy(line, invalidSection), invalidSection))
                .withMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("상행역 기준으로 신규 구간을 추가한다")
        void addSectionByUpStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, seolleungStation, 10);
            line.addSection(initialSection);

            Section newSection = new Section(line, gangnamStation, yeoksamStation, 5);

            // when
            line.addSection(getStrategy(line, newSection), newSection);

            // then
            assertThat(line.getUnmodifiableSections()).contains(newSection);
        }

        @Test
        @DisplayName("하행역 기준으로 신규 구간을 추가한다")
        void addSectionByDownStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, seolleungStation, 10);
            line.addSection(initialSection);

            Section newSection = new Section(line, yeoksamStation, seolleungStation, 5);

            // when
            line.addSection(getStrategy(line, newSection), newSection);

            // then
            assertThat(line.getUnmodifiableSections()).contains(newSection);
        }

        @Test
        @DisplayName("상행역 기준으로 구간 추가 시 기존 구간 거리보다 큰 거리값을 요청하면 실패한다")
        void addSectionWithShorterDistanceThanExistingUpStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, seolleungStation, 10);
            line.addSection(initialSection);

            Section invalidSection = new Section(line, gangnamStation, yeoksamStation, 15);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(getStrategy(line, invalidSection), invalidSection))
                .withMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("하행역 기준으로 구간 추가 시 기존 구간 거리보다 큰 거리값을 요청하면 실패한다")
        void addSectionWithShorterDistanceThanExistingDownStation() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, seolleungStation, 10);
            line.addSection(initialSection);

            Section invalidSection = new Section(line, yeoksamStation, seolleungStation, 15);

            // when // then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.addSection(getStrategy(line, invalidSection), invalidSection))
                .withMessageContaining(CANNOT_ADD_SECTION_MESSAGE);
        }

        @Test
        @DisplayName("구간 추가 후 마지막 구간에 다시 구간을 추가한다")
        void addSectionToEndOfLine() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");
            Station hantiStation = new Station("한티역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            Section newSection = new Section(line, seolleungStation, hantiStation, 7);

            // when
            line.addSection(getStrategy(line, newSection), newSection);

            // then
            assertThat(line.getUnmodifiableSections()).contains(newSection);
        }
    }

    private SectionAdditionStrategy getStrategy(Line line, Section initialSection) {
        return sectionAdditionStrategyFactory.getStrategy(line, initialSection);
    }

    @Nested
    @DisplayName("구간 삭제 기능")
    class RemoveSection {
        @Test
        @DisplayName("첫 구간의 상행역을 제거하려고 하면 첫 구간이 제거된다")
        void removeFirstSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            // when
            line.removeSection(gangnamStation);

            // then
            assertThat(line.getOrderedUnmodifiableSections()).containsExactly(additionalSection);
        }

        @Test
        @DisplayName("마지막 구간의 하행역을 제거하려고 하면 마지막 구간이 제거된다")
        void removeLastSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            // when
            line.removeSection(seolleungStation);

            // then
            assertThat(line.getOrderedUnmodifiableSections()).containsExactly(initialSection);
        }

        @Test
        @DisplayName("중간에 존재하는 역을 제거하려고 하면 해당 역을 상행역으로 가지고 있는 구간이 제거된다")
        void removeNextSectionSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section additionalSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(additionalSection);

            // when
            line.removeSection(yeoksamStation);

            // then
            List<Section> orderedSections = line.getOrderedUnmodifiableSections();

            assertThat(orderedSections).containsExactly(initialSection);
            assertThat(orderedSections).doesNotContain(additionalSection);
            assertThat(initialSection.getUpStation()).isEqualTo(gangnamStation);
            assertThat(initialSection.getDownStation()).isEqualTo(seolleungStation);
            assertThat(initialSection.getDistance()).isEqualTo(18);
        }

        @Test
        @DisplayName("존재하지 않는 구간을 제거하려고 하면 실패한다")
        void removeNonExistentSectionFails() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Station seolleungStation = new Station("선릉역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            Section newSection = new Section(line, yeoksamStation, seolleungStation, 8);

            line.addSection(initialSection);
            line.addSection(newSection);

            // when // then
            Station nonExistentStation = new Station("없는역");
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.removeSection(nonExistentStation))
                .withMessage(NO_SECTION_TO_REMOVE_STATION_MESSAGE);
        }
    }

    @Nested
    @DisplayName("구간 조회 기능")
    class GetSection {

        @Test
        @DisplayName("노선의 마지막 구간을 조회할 때 마지막 구간이 반환된다")
        void getLastSection() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");
            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            // when
            Sections sections = line.getSections();

            // then
            assertThat(sections.getSections()).containsExactly(initialSection);
        }


        @Test
        @DisplayName("구간 리스트를 조회할 때 변경 불가능한 리스트가 반환된다")
        void getUnmodifiableSections() {
            // given
            Line line = new Line("2호선", "bg-red-600");
            Station gangnamStation = new Station("강남역");
            Station yeoksamStation = new Station("역삼역");

            Section initialSection = new Section(line, gangnamStation, yeoksamStation, 10);
            line.addSection(initialSection);

            // when
            List<Section> sections = line.getUnmodifiableSections();

            // then
            assertThat(sections).contains(initialSection);

            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> sections.add(
                    new Section(
                        line,
                        new Station("선릉역"),
                        new Station("잠실역"),
                        5
                    )
                )
            );
        }
    }


    @Nested
    @DisplayName("노선 정보 업데이트 기능")
    class UpdateLine {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("노선 정보를 업데이트 할 때 빈 노선 이름을 입력하면 오류가 발생한다")
        void updateLineWithEmptyName(String name) {
            // given
            Line line = new Line("2호선", "bg-red-600");

            // when & then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.getUpdated(name, "bg-orange-100"))
                .withMessage(EMPTY_NAME_ERROR_MESSAGE);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("노선 정보를 업데이트 할 때 빈 노선 색상을 입력하면 오류가 발생한다")
        void updateLineWithEmptyColor(String color) {
            // given
            Line line = new Line("2호선", "bg-red-600");

            // when & then
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> line.getUpdated("3호선", color))
                .withMessage(EMPTY_COLOR_ERROR_MESSAGE);
        }

        @Test
        @DisplayName("노선 정보를 업데이트 할 때 올바른 이름과 색상을 주면 업데이트가 성공한다")
        void updateLineSuccessfully() {
            // given
            Line line = new Line("2호선", "bg-red-600");

            // when
            Line updatedLine = line.getUpdated("3호선", "bg-blue-600");

            // then
            assertThat(updatedLine.getName()).isEqualTo("3호선");
            assertThat(updatedLine.getColor()).isEqualTo("bg-blue-600");
        }

    }
}
