package nextstep.subway.unit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static nextstep.subway.unit.LineFixture.makeLine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.line.exception.LineAppendSectionException;
import nextstep.subway.domain.line.exception.LineRemoveSectionException;
import nextstep.subway.domain.station.Station;

class LineTest {
    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 선릉역 = new Station("선릉역");
    private static final Station 삼성역 = new Station("삼성역");

    @DisplayName("구간을 추가한다")
    @Nested
    class AppendSectionTest {

        @Nested
        class Success {

            @Test
            void 구간을_추가한다() {
                final var line = makeLine(강남역, 역삼역);

                final var section = new Section(line, 역삼역, 선릉역, 10);
                line.appendSection(section);

                final var actual = line.getStations();
                assertThat(actual).containsExactly(강남역, 역삼역, 선릉역);
            }
        }

        @Nested
        class Fail {

            @Test
            void 새로운_구간의_상행역은_노선의_하행종점역과_일치해야_한다() {
                final var line = makeLine(강남역, 역삼역);

                final var section = new Section(line, 강남역, 선릉역, 10);
                assertThatThrownBy(() -> line.appendSection(section))
                        .isInstanceOf(LineAppendSectionException.class);
            }

            @Test
            void 새로운_구간의_하행역은_노선에_포함되지_않은_역이어야_한다() {
                final var line = makeLine(강남역, 역삼역, 선릉역);

                final var section = new Section(line, 선릉역, 강남역, 10);
                assertThatThrownBy(() -> line.appendSection(section))
                        .isInstanceOf(LineAppendSectionException.class);
            }
        }
    }

    @DisplayName("구간을 삭제한다")
    @Nested
    class RemoveSectionTest {

        @Nested
        class Success {

            @Test
            void 구간을_삭제한다() {
                final var line = makeLine(강남역, 역삼역, 선릉역);
                line.removeSection(선릉역);

                final var actual = line.getStations();
                assertThat(actual).containsExactly(강남역, 역삼역);
            }
        }

        @Nested
        class Fail {

            @Test
            void 삭제하고자_하는_역은_노선의_하행역이어야만_한다() {
                final var line = makeLine(강남역, 역삼역, 선릉역);

                assertThatThrownBy(() -> line.removeSection(역삼역))
                        .isInstanceOf(LineRemoveSectionException.class);
            }

            @Test
            void 노선의_구간이_하나만_있는_경우_하행역을_삭제할_수_없다() {
                final var line = makeLine(강남역, 역삼역);

                assertThatThrownBy(() -> line.removeSection(역삼역))
                        .isInstanceOf(LineRemoveSectionException.class);
            }
        }
    }

    @DisplayName("구간 내 역 목록을 반환한다")
    @Test
    void getStations() {
        final var line = makeLine(강남역, 역삼역, 선릉역, 삼성역);

        final var actual = line.getStations();
        assertThat(actual).containsExactly(강남역, 역삼역, 선릉역, 삼성역);
    }
}
