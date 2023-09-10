package nextstep.subway.unit;

import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.fixture.LineFixture.분당선_색;
import static nextstep.subway.fixture.LineFixture.분당선_이름;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    Line 분당선;

    /**
     * Given 지하철역은 만들어져 있다.
     */
    @BeforeEach
    void setup() {
        분당선 = new Line(분당선_이름, 분당선_색);
    }

    @Test
    @DisplayName("구간 최초 추가")
    void addSection() {
        //when
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);

        //then
        assertThat(분당선.getEndStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("구간 상행 종점 추가")
    void addStartSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);

        //when
        Section 역삼역_선릉역_구간 = new Section(분당선, 교대역, 강남역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //then
        assertThat(분당선.getStartStation()).isEqualTo(교대역);
    }

    @Test
    @DisplayName("구간 하행 종점 추가")
    void addLastSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);

        //when
        Section 교대역_강남역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(교대역_강남역_구간);

        //then
        assertThat(분당선.getEndStation()).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("구간 중간 추가")
    void addMiddleSection() {
        //given
        Section 강남역_선릉역_구간 = new Section(분당선, 강남역, 선릉역, 10);
        분당선.addSection(강남역_선릉역_구간);

        //when
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 4);
        분당선.addSection(역삼역_선릉역_구간);

        //then
        List<Section> sections = 분당선.getSections().getSections();
        assertThat(sections).hasSize(2);
        Optional<Section> 변경된_강남역_역삼역_구간 = sections.stream().filter(section ->
                section.getUpStation().equals(강남역)).findFirst();
        Optional<Section> 변경된_역삼역_선릉역_구간 = sections.stream().filter(section ->
                section.getUpStation().equals(역삼역)).findFirst();

        assertThat(변경된_강남역_역삼역_구간.get().getUpStation()).isEqualTo(강남역);
        assertThat(변경된_강남역_역삼역_구간.get().getDownStation()).isEqualTo(역삼역);
        assertThat(변경된_강남역_역삼역_구간.get().getDistance()).isEqualTo(6);

        assertThat(변경된_역삼역_선릉역_구간.get().getUpStation()).isEqualTo(역삼역);
        assertThat(변경된_역삼역_선릉역_구간.get().getDownStation()).isEqualTo(선릉역);
        assertThat(변경된_역삼역_선릉역_구간.get().getDistance()).isEqualTo(4);
    }

    @Test
    @DisplayName("구간 추가 실패 - 길이 제약")
    void addLongSection() {
        //given
        Section 강남역_선릉역_구간 = new Section(분당선, 강남역, 선릉역, 10);
        분당선.addSection(강남역_선릉역_구간);

        //when then
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 12);
        assertThatThrownBy(() -> 분당선.addSection(역삼역_선릉역_구간)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("구간 추가 실패 - 역이 하나도 포함되어 있지 않은경우")
    void addNotExistsSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);

        //when then
        Section 선릉역_교대역_구간 = new Section(분당선, 선릉역, 교대역, 4);
        assertThatThrownBy(() -> 분당선.addSection(선릉역_교대역_구간)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("구간 추가 실패 - 역이 모두 등록되어 있는 경우_붙어있는 구간")
    void addAllExistsSection1() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when then
        assertThatThrownBy(() -> 분당선.addSection(강남역_역삼역_구간)).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("구간 추가 실패 - 역이 모두 등록되어 있는 경우_떨어져있는 구간")
    void addAllExistsSection2() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when then
        Section 강남역_선릉역_구간 = new Section(분당선, 강남역, 선릉역, 9);
        assertThatThrownBy(() -> 분당선.addSection(강남역_선릉역_구간)).isInstanceOf(BusinessException.class);
    }


    @Test
    @DisplayName("역 조회")
    void getStations() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when
        List<Station> stations = 분당선.getStations();

        //then
        assertThat(stations).containsAnyOf(강남역, 역삼역, 선릉역);
    }

    @Test
    @DisplayName("구간 삭제 성공 - 하행 종점")
    void removeLastSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when
        분당선.removeSection(선릉역);

        //then
        assertThat(분당선.getSections().getSections()).containsAnyOf(강남역_역삼역_구간);
    }

    @Test
    @DisplayName("구간 삭제 성공 - 중간")
    void removeMiddleSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when
        분당선.removeSection(역삼역);

        //then
        assertThat(분당선.getSections().getStations()).containsAnyOf(강남역, 선릉역);
    }

    @Test
    @DisplayName("구간 삭제 성공 - 상행 종점")
    void removeFirstSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when
        분당선.removeSection(강남역);

        //then
        assertThat(분당선.getSections().getSections()).containsAnyOf(역삼역_선릉역_구간);
    }

    @Test
    @DisplayName("구간 삭제 실패 - 구간이 1개")
    void removeOnlyOneSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);

        //when //then
        assertThrows(BusinessException.class, () -> 분당선.removeSection(강남역));
    }

    @Test
    @DisplayName("구간 삭제 실패 - 존재하지 않는 구간")
    void removeNotExistsSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(역삼역_선릉역_구간);

        //when //then
        assertThrows(BusinessException.class, () -> 분당선.removeSection(삼성역));
    }
}
