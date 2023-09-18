package nextstep.subway.unit;

import nextstep.subway.common.exception.ContainsAllStationException;
import nextstep.subway.common.exception.NotExistStationException;
import nextstep.subway.common.exception.OnlyOneSectionException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.LineFixture.분당선_색;
import static nextstep.subway.fixture.LineFixture.분당선_이름;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    Line 분당선;

    /**
     * Given 지하철역은 만들어져 있다.
     */
    @BeforeEach
    void setup() {
        // Given 노선을 추가한다.
        분당선 = new Line(분당선_이름, 분당선_색);
    }

    @Test
    @DisplayName("구간들의 역을 순서대로 반환")
    void getStations() {
        // given 구간을 2개 추가한다.
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(강남역_역삼역_구간);
        분당선.addSection(역삼역_선릉역_구간);

        // when
        List<Station> stations = 분당선.getStations();

        //then
        assertThat(stations).containsExactly(강남역, 역삼역, 선릉역);
    }

    @Test
    @DisplayName("구간 추가 - 구간이 비어있다.")
    void addSectionEmpty() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);

        // when
        분당선.addSection(강남역_역삼역_구간);

        // then
        assertThat(분당선.getStations()).hasSize(2);
        assertThat(분당선.getStartStation()).isEqualTo(강남역);
        assertThat(분당선.getEndStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("구간 추가 - 상행 종점 추가")
    void addSectionStart() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 교대역_강남역_구간 = new Section(분당선, 교대역, 강남역, 10);
        분당선.addSection(강남역_역삼역_구간);

        // when
        분당선.addSection(교대역_강남역_구간);

        // then
        assertThat(분당선.getStations()).hasSize(3);
        assertThat(분당선.getStartStation()).isEqualTo(교대역);
    }

    @Test
    @DisplayName("구간 추가 - 하행 종점 추가")
    void addSectionEnd() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(강남역_역삼역_구간);

        // when
        분당선.addSection(역삼역_선릉역_구간);

        // then
        assertThat(분당선.getStations()).hasSize(3);
        assertThat(분당선.getEndStation()).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("구간 추가 - 중간 추가")
    void addSectionMiddle() {
        // given
        Section 교대역_강남역_구간 = new Section(분당선, 교대역, 강남역, 10);
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_삼성역_구간 = new Section(분당선, 역삼역, 삼성역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 5);
        분당선.addSection(교대역_강남역_구간);
        분당선.addSection(강남역_역삼역_구간);
        분당선.addSection(역삼역_삼성역_구간);

        // when
        분당선.addSection(역삼역_선릉역_구간);

        // then
        assertThat(분당선.getStations()).containsExactly(교대역, 강남역, 역삼역, 선릉역, 삼성역);
    }

    @Test
    @DisplayName("구간 추가 실패 - 이미 존재하는 상하행역")
    void addSectionExists() {
        // given
        Section 교대역_강남역_구간 = new Section(분당선, 교대역, 강남역, 10);
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 교대역_역삼역_구간 = new Section(분당선, 교대역, 역삼역, 5);
        분당선.addSection(교대역_강남역_구간);
        분당선.addSection(강남역_역삼역_구간);

        // when then
        assertThatThrownBy(() -> 분당선.addSection(교대역_역삼역_구간)).isInstanceOf(ContainsAllStationException.class);
    }

    @Test
    @DisplayName("구간 삭제 - 상행 좀점 삭제")
    void removeSectionFirst() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(강남역_역삼역_구간);
        분당선.addSection(역삼역_선릉역_구간);

        // when
        분당선.removeSection(강남역);

        // then
        assertThat(분당선.getStations()).containsExactly(역삼역, 선릉역);
        assertThat(분당선.getStartStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("구간 삭제 - 하행 좀점 삭제")
    void removeSectionEnd() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(강남역_역삼역_구간);
        분당선.addSection(역삼역_선릉역_구간);

        // when
        분당선.removeSection(선릉역);

        // then
        assertThat(분당선.getStations()).containsExactly(강남역, 역삼역);
        assertThat(분당선.getEndStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("구간 삭제 - 중간 삭제")
    void removeSectionMiddle() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        Section 선릉역_삼성역_구간 = new Section(분당선, 선릉역, 삼성역, 10);
        분당선.addSection(강남역_역삼역_구간);
        분당선.addSection(역삼역_선릉역_구간);
        분당선.addSection(선릉역_삼성역_구간);

        // when
        분당선.removeSection(선릉역);

        // then
        assertThat(분당선.getStations()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    @DisplayName("구간 삭제 실패 - 구간이 한개")
    void removeSectionOnlyOne() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSection(강남역_역삼역_구간);

        // when then
        assertThatThrownBy(() -> 분당선.removeSection(강남역)).isInstanceOf(OnlyOneSectionException.class);
    }

    @Test
    @DisplayName("구간 삭제 실패 - 노선에 등록되지 않은 역")
    void removeSectionNotExists() {
        // given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSection(강남역_역삼역_구간);
        분당선.addSection(역삼역_선릉역_구간);

        // when then
        assertThatThrownBy(() -> 분당선.removeSection(삼성역)).isInstanceOf(NotExistStationException.class);
    }


}