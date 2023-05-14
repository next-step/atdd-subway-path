package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    private Line 강남_2호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        강남_2호선 = new Line("2호선", "green");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
    }

    @ParameterizedTest
    @ValueSource(ints = {11, 20})
    void 기존_역_사이_길이보다_크거나_같으면_구간을_추가_할_수_없다(int 강남_삼성_구간_거리) {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 11);
        Section 강남_삼성_구간 = new Section(강남_2호선, 강남역, 삼성역, 강남_삼성_구간_거리);
        강남_2호선.addSection(강남_역삼_구간);

        // when
        assertThatThrownBy(() -> 강남_2호선.addSection(강남_삼성_구간))
                .isInstanceOf(SectionBadRequestException.class)
                .hasMessage("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어있지_않으면_구간을_추가_할_수_없다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        강남_2호선.addSection(강남_역삼_구간);

        Section 새로운_강남_역삼_구간 = new Section(강남_2호선, new Station("장승배기"), new Station("천호역"), 12);

        // when & then
        assertThatThrownBy(() -> 강남_2호선.addSection(새로운_강남_역삼_구간))
                .isInstanceOf(SectionBadRequestException.class)
                .hasMessage("상행역과 하행역이 둘 중 하나라도 기존 구간에 포함 되어 있어야 합니다.");
    }

    @Test
    void 기존_구간과_신규_구간이_같다면_구간을_추가_할_수_없다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 역삼_삼성_구간 = new Section(강남_2호선, 역삼역, 삼성역, 8);
        강남_2호선.addSection(강남_역삼_구간);
        강남_2호선.addSection(역삼_삼성_구간);

        Section 새로운_강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 12);

        // when & then
        assertThatThrownBy(() -> 강남_2호선.addSection(새로운_강남_역삼_구간))
                .isInstanceOf(SectionBadRequestException.class)
                .hasMessage("상행역과 하행역이 둘 다 이미 등록 되어 있습니다.");
    }

    @Test
    void 구간이_비어있는경우_구간을_추가한다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);

        // when
        강남_2호선.addSection(강남_역삼_구간);

        // then
        assertThat(강남_2호선.getSections()).containsExactly(강남_역삼_구간);
    }

    @Test
    void 새로운_구간의_상행역을_상행_종점으로_등록한다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 삼성_강남_구간 = new Section(강남_2호선, 삼성역, 강남역, 7);
        강남_2호선.addSection(강남_역삼_구간);

        // when
        강남_2호선.addSection(삼성_강남_구간);

        // then
        assertThat(강남_2호선.getSections()).contains(삼성_강남_구간);
    }

    @Test
    void 새로운_구간의_하행역을_하행_종점으로_등록한다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 역삼_삼성_구간 = new Section(강남_2호선, 역삼역, 삼성역, 7);
        강남_2호선.addSection(강남_역삼_구간);

        // when
        강남_2호선.addSection(역삼_삼성_구간);

        // then
        assertThat(강남_2호선.getSections()).contains(역삼_삼성_구간);
    }

    @Test
    void 기존_구간의_상행역에_새로운_구간을_추가한다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 강남_삼성_구간 = new Section(강남_2호선, 강남역, 삼성역, 7);
        강남_2호선.addSection(강남_역삼_구간);

        // when
        강남_2호선.addSection(강남_삼성_구간);

        assertThat(강남_2호선.getStations()).extracting("name").contains("강남역", "역삼역", "삼성역");
    }

    @Test
    void 기존_구간의_하행역에_새로운_구간을_추가한다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 삼성_역삼_구간 = new Section(강남_2호선, 삼성역, 역삼역, 7);
        강남_2호선.addSection(강남_역삼_구간);

        // when
        강남_2호선.addSection(삼성_역삼_구간);

        assertThat(강남_2호선.getStations()).extracting("name").contains("강남역", "역삼역", "삼성역");
    }

    @Test
    void 노선의_역_목록을_반환한다() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 역삼_삼성_구간 = new Section(강남_2호선, 역삼역, 삼성역, 12);
        강남_2호선.addSection(강남_역삼_구간);
        강남_2호선.addSection(역삼_삼성_구간);

        // when
        List<Station> 역_목록 = 강남_2호선.getStations();

        // then
        assertThat(역_목록).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    void 노선에_구간이_비어있는_경우_구간을_삭제_할_수_없다() {
        assertThatThrownBy(() -> 강남_2호선.removeSection(삼성역))
                .isInstanceOf(SectionBadRequestException.class)
                .hasMessage("구간이 존재하지 않습니다.");
    }

    @Test
    void 노선의_구간이_1개일_경우_구간을_삭제_할_수_없다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));

        // then
        assertThatThrownBy(() -> 강남_2호선.removeSection(삼성역))
                .isInstanceOf(SectionBadRequestException.class)
                .hasMessage("현재 노선은 구간이 1개 입니다.");
    }

    @Test
    void 노선에_등록되지_않은_구간을_삭제_할_수_없다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 12));

        // then
        assertThatThrownBy(() -> 강남_2호선.removeSection(new Station("청명역")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("역이 존재 하지 않습니다.");
    }

    @Test
    void 노선의_하행_종점을_삭제한다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 12));

        // when
        강남_2호선.removeSection(삼성역);

        // then
        assertAll(
                () -> assertThat(강남_2호선.getSections().size()).isEqualTo(1),
                () -> assertThat(강남_2호선.getStations()).isNotIn(삼성역)
        );

    }

    @Test
    void 노선의_상행_종점을_삭제한다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 12));

        // when
        강남_2호선.removeSection(강남역);

        // then
        assertAll(
                () -> assertThat(강남_2호선.getSections().size()).isEqualTo(1),
                () -> assertThat(강남_2호선.getStations()).isNotIn(강남역)
        );
    }

    @Test
    void 노선의_중간역을_삭제한다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 12));

        // when
        강남_2호선.removeSection(역삼역);

        // then
        assertAll(
                () -> assertThat(강남_2호선.getSections().size()).isEqualTo(1),
                () -> assertThat(강남_2호선.getStations()).isNotIn(역삼역)
        );
    }

    @Test
    void 노선의_중간역을_삭제하면_거리는_두_구간의_거리의_합이_된다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 12));

        // when
        강남_2호선.removeSection(역삼역);

        // then
        assertAll(
                () -> assertThat(강남_2호선.getSections().size()).isEqualTo(1),
                () -> assertThat(강남_2호선.getStations()).isNotIn(역삼역),
                () -> assertThat(강남_2호선.getDistance()).isEqualTo(22)
        );
    }

    @Test
    void 노선의_이름과_색을_수정한다() {
        // when
        강남_2호선.update("강남강남_2호선", "super green");

        assertThat(강남_2호선.getName()).isEqualTo("강남강남_2호선");
        assertThat(강남_2호선.getColor()).isEqualTo("super green");
    }

    @Test
    void 기존_구간의_역에_새로운_구간을_추가하는경우_새롭게_추가되는_구간의_길이는_기존구간의_길이를_뺀_나머지_길이이다() {
        // given
        int 강남_역삼_구간_거리 = 10;
        int 강남_삼성_구간_거리 = 7;
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 강남_역삼_구간_거리);
        Section 강남_삼성_구간 = new Section(강남_2호선, 강남역, 삼성역, 강남_삼성_구간_거리);

        강남_2호선.addSection(강남_역삼_구간);

        // when
        강남_2호선.addSection(강남_삼성_구간);

        // then
        int 계산된_강남_삼성_구간_거리 = 강남_2호선.getSections().get(0).getDistance();
        assertThat(계산된_강남_삼성_구간_거리).isEqualTo(강남_역삼_구간_거리 - 강남_삼성_구간_거리);
    }

    @Test
    void 노선의_구간을_반환한다() {
        // given
        int 강남_역삼_구간_거리 = 10;
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 강남_역삼_구간_거리);
        강남_2호선.addSection(강남_역삼_구간);

        // when & then
        assertThat(강남_2호선.getDistance()).isEqualTo(강남_역삼_구간_거리);
    }
}
