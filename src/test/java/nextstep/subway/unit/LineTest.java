package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LineTest {

    private Line line;
    private long 강남역Id = 1L;
    private long 역삼역Id = 2L;


    @DisplayName("section 추가")
    @Nested
    class addSection {

        private final Section section = new Section(line, 강남역Id, 역삼역Id, 10);

        @Test
        void 구간이_없는_경우_추가_가능하다() {
            line = new Line("이호선", "초록색");

            assertDoesNotThrow(() -> line.addSection(section));
        }

        @Test
        void 추가할_구간의_하행역이_이미_등록되어_있다면_추가할_수_없다() {
            line = new Line("이호선", "초록색", section);
            final Section section1 = new Section(line, 3L, 강남역Id, 10);

            assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(section1));
        }

        @Test
        void 추가할_구간의_상행역_하행_종점역과_다르다면_추가할_수_없다() {
            line = new Line("이호선", "초록색", section);
            final Section section1 = new Section(line, 3L, 4L, 10);

            assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(section1));
        }

        @Test
        void 추가할_구간의_상행역_하행_종점역과_같다면_추가할_수_있다() {
            line = new Line("이호선", "초록색", section);
            final Section section1 = new Section(line, 역삼역Id, 3L, 10);

            assertDoesNotThrow(() -> line.addSection(section1));
        }
    }

    @DisplayName("section 삭제")
    @Nested
    class removeSection {

        private final long 삼성역Id = 3L;
        private final Section section1 = new Section(line, 강남역Id, 역삼역Id, 10);
        private final Section section2 = new Section(line, 역삼역Id, 삼성역Id, 8);

        @Test
        void 구간이_하나인_경우_삭제할_수_없다() {
            line = new Line("이호선", "초록색", section1);
            assertThatIllegalArgumentException()
                .isThrownBy(() -> line.removeSection(역삼역Id));
        }

        @Test
        void 하행_종점역이_아닌_경우_삭제할_수_없다() {
            line = new Line("이호선", "초록색", section1, section2);
            assertThatIllegalArgumentException()
                .isThrownBy(() -> line.removeSection(역삼역Id));
        }

        @Test
        void 하행_종점역인_경우_삭제할_수_있다() {
            line = new Line("이호선", "초록색", section1, section2);

            assertDoesNotThrow(() -> line.removeSection(삼성역Id));
        }
    }
}
