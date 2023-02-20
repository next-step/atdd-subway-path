package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class SectionsTest {

    private final Line 이호선 = new Line("이호선", "초록색");

    private final long 강남역 = 1L;
    private final long 역삼역 = 2L;
    private final long 삼성역 = 3L;
    private final long 잠실역 = 4L;
    private final long 잠실새내역 = 5L;
    private final long 건대역 = 6L;

    private final Section 강남역_역삼역 = new Section(1L, 이호선, 강남역, 역삼역, 10);
    private final Section 역삼역_삼성역 = new Section(2L, 이호선, 역삼역, 삼성역, 10);
    private final Section 삼성역_잠실역 = new Section(3L, 이호선, 삼성역, 잠실역, 10);
    private final Section 삼성역_잠실새내역 = new Section(이호선, 삼성역, 잠실새내역, 5);
    private final Section 잠실새내역_잠실역 = new Section(이호선, 잠실새내역, 잠실역, 5);
    private final Section 잠실역_건대역 = new Section(6L, 이호선, 잠실역, 건대역, 5);

    @Test
    void 상행역_하행역이_이미_노선에_등록되어_있다면_등록할_수_없다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실역);

        final Section 역삼역_잠실역 = new Section(이호선, 역삼역, 잠실역, 5);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(역삼역_잠실역));
    }

    @Test
    void 역_사이_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록할_수_없다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실역, 잠실역_건대역);
        final Section 삼성역_잠실새내역 = new Section(이호선, 삼성역, 잠실새내역, 10);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(삼성역_잠실새내역));
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_등록할_수_없음() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.add(잠실역_건대역));
    }

    @Test
    void 상행_종점으로_등록_가능하다() {
        final Sections sections = new Sections(역삼역_삼성역, 삼성역_잠실역, 잠실역_건대역);
        sections.add(강남역_역삼역);

        assertThat(sections)
            .isEqualTo(
                new Sections(
                    강남역_역삼역,
                    역삼역_삼성역,
                    삼성역_잠실역,
                    잠실역_건대역
                )
            );
    }

    @Test
    void 하행_종점으로_등록_가능하다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실역);
        sections.add(잠실역_건대역);

        assertThat(sections)
            .isEqualTo(
                new Sections(
                    강남역_역삼역,
                    역삼역_삼성역,
                    삼성역_잠실역,
                    잠실역_건대역
                )
            );
    }

    @Test
    void 역_사이_새로운_역을_등록_가능하다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실역, 잠실역_건대역);
        sections.add(삼성역_잠실새내역);

        assertThat(sections)
            .isEqualTo(
                new Sections(
                    강남역_역삼역,
                    역삼역_삼성역,
                    삼성역_잠실새내역,
                    잠실새내역_잠실역,
                    잠실역_건대역
                )
            );
    }

    @Test
    void 구간이_하나만_존재하면_삭제할_수_없다() {
        final Sections sections = new Sections(강남역_역삼역);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> sections.remove(역삼역));
    }

    @Test
    void 중간_구간을_제거할_수_있다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실새내역, 잠실새내역_잠실역);
        sections.remove(잠실새내역);

        assertThat(sections)
            .isEqualTo(
                new Sections(
                    강남역_역삼역,
                    역삼역_삼성역,
                    삼성역_잠실역
                )
            );
    }

    @Test
    void 첫_구간을_제거할_수_있다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실새내역, 잠실새내역_잠실역);
        sections.remove(강남역);

        assertThat(sections)
            .isEqualTo(
                new Sections(
                    역삼역_삼성역,
                    삼성역_잠실새내역,
                    잠실새내역_잠실역
                )
            );
    }

    @Test
    void 마지막_구간을_제거할_수_있다() {
        final Sections sections = new Sections(강남역_역삼역, 역삼역_삼성역, 삼성역_잠실새내역, 잠실새내역_잠실역);
        sections.remove(잠실역);

        assertThat(sections)
            .isEqualTo(
                sections(
                    강남역_역삼역,
                    역삼역_삼성역,
                    삼성역_잠실새내역
                )
            );
    }

    private Sections sections(Section... elements) {
        final Sections sections = new Sections();
        for (Section element : elements) {
            sections.add(element);
        }
        return sections;
    }
}
