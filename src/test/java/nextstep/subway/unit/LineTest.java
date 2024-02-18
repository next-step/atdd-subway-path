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
            .set("sections", new Sections())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);

        final var 구간 = new Section(노선, 첫번째역, 두번째역, 10);

        // when
        노선.addSection(구간);

        // then
        assertThat(노선.getSections().size()).isEqualTo(1);
        assertThat(노선.getSections().contains(구간)).isTrue();
    }

    @DisplayName("지하철 구간을 생성 시 노선에 이미 존재하는 구간인 경우 에러가 발생한다.")
    @Test
    void 노선_구간_추가_실패_중복_구간() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", new Sections())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);
        final var 구간 = new Section(노선, 첫번째역, 두번째역, 10);
        노선.addSection(구간);

        // when
        Throwable throwable = catchThrowable(() -> 노선.addSection(구간));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class)
            .hasMessageContaining("이미 노선에 속한 구간을 추가할 수 없습니다.");
    }

    @DisplayName("지하철 노선의 기존 구간에 연결되지 않는 구간을 추가할 수 없다.")
    @Test
    void 노선_구간_추가_실패_연결되지_않는_구간() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", new Sections())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);
        final var 첫번째구간 = new Section(노선, 첫번째역, 두번째역, 10);
        노선.addSection(첫번째구간);

        final var 세번째역 = FixtureUtil.getFixture(Station.class);
        final var 네번째역 = FixtureUtil.getFixture(Station.class);

        // when
        Throwable throwable = catchThrowable(() -> 노선.addSection(new Section(노선, 세번째역, 네번째역, 10)));

        // then
        assertThat(throwable).isInstanceOf(BusinessException.class)
            .hasMessageContaining("노선에 새로운 구간과 이어지는 역이 없습니다.");
    }

    @DisplayName("노선에 등록된 구간을 조회한다.")
    @Test
    void getStations() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", new Sections())
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
        assertThat(구간_목록.getSections()).containsExactly(첫번째구간, 두번째구간);
    }

    @DisplayName("노선에 등록된 구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        final var 노선 = FixtureUtil.getBuilder(Line.class)
            .set("name", "신분당선")
            .set("color", "빨강")
            .set("sections", new Sections())
            .sample();

        final var 첫번째역 = FixtureUtil.getFixture(Station.class);
        final var 두번째역 = FixtureUtil.getFixture(Station.class);
        final var 세번째역 = FixtureUtil.getFixture(Station.class);

        노선.addSection(new Section(노선, 첫번째역, 두번째역, 10));
        final var 두번째구간 = new Section(노선, 두번째역, 세번째역, 10);
        노선.addSection(두번째구간);

        // when
        노선.removeSection(두번째역.getId());

        // then
        assertThat(노선.getSections().size()).isEqualTo(1);
        // TODO 2단계 과제
//        assertThat(노선.getSections().contains(두번째구간)).isFalse();
    }
}
