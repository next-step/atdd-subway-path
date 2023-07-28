package nextstep.subway.unit;

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
        분당선.addSections(강남역_역삼역_구간);

        //then
        assertThat(분당선.getEndStation()).isEqualTo(역삼역);
    }

    @Test
    @DisplayName("구간 상행 종점 추가")
    void addStartSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSections(강남역_역삼역_구간);

        //when
        Section 역삼역_선릉역_구간 = new Section(분당선, 교대역, 강남역, 10);
        분당선.addSections(역삼역_선릉역_구간);

        //then
        assertThat(분당선.getStartStation()).isEqualTo(교대역);
    }

    @Test
    @DisplayName("구간 하행 종점 추가")
    void addLastSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSections(강남역_역삼역_구간);

        //when
        Section 교대역_강남역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSections(교대역_강남역_구간);

        //then
        assertThat(분당선.getEndStation()).isEqualTo(선릉역);
    }


    @Test
    @DisplayName("역 조회")
    void getStations() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSections(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSections(역삼역_선릉역_구간);

        //when
        List<Station> stations = 분당선.getStations();

        //then
        assertThat(stations).containsAnyOf(강남역, 역삼역, 선릉역);
    }

    @Test
    @DisplayName("구간 삭제")
    void removeSection() {
        //given
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSections(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(분당선, 역삼역, 선릉역, 10);
        분당선.addSections(역삼역_선릉역_구간);

        //when
        분당선.removeSection(선릉역);

        //then
        assertThat(분당선.getSections()).containsAnyOf(강남역_역삼역_구간);
    }
}
