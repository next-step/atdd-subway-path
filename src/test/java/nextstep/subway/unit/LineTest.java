package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.BusinessException;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void 노선에_구간을_추가한다() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", Collections.emptyList())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);

        final var 구간 = new Section(노선, 첫번째역, 두번째역, 10);

        // when
        노선.addSection(구간);

        // then
        assertThat(노선.getSections().size()).isEqualTo(1);
        assertThat(노선.getSections()).contains(구간);
    }

    @DisplayName("노선에 등록된 구간을 조회한다.")
    @Test
    void getStations() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", Collections.emptyList())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);
        final var 세번째역 = FixtureUtil.getFixture(Station.class);

        final var 첫번째구간 = new Section(노선, 첫번째역, 두번째역, 10);
        final var 두번째구간 = new Section(노선, 두번째역, 세번째역, 10);

        노선.addSection(첫번째구간);
        노선.addSection(두번째구간);

        // when
        final var 구간_목록 = 노선.getSections();

        // then
        assertThat(구간_목록.size()).isEqualTo(2);
        assertThat(구간_목록).containsExactly(첫번째구간, 두번째구간);
    }

    @DisplayName("노선에 등록된 구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", Collections.emptyList())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);
        final var 세번째역 = FixtureUtil.getFixture(Station.class);

        노선.addSection(new Section(노선, 첫번째역, 두번째역, 10));
        final var 두번째구간 = new Section(노선, 두번째역, 세번째역, 10);
        노선.addSection(두번째구간);

        // when
        노선.removeSection(두번째구간);

        // then
        assertThat(노선.getSections().size()).isEqualTo(1);
        assertThat(노선.getSections()).doesNotContain(두번째구간);
    }
}
