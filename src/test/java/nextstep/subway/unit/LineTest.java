package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Line 분당선;

    /**
     * Given 지하철역은 만들어져 있다.
     */
    @BeforeEach
    void setup() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        분당선 = new Line("분당선", "Yellow");
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        //when
        Section 강남역_역삼역_구간 = new Section(분당선, 강남역, 역삼역, 10);
        분당선.addSections(강남역_역삼역_구간);

        //then
        List<Section> sections = 분당선.getSections();
        Section lastSection = sections.get(sections.size() - 1);
        assertThat(lastSection).isEqualTo(강남역_역삼역_구간);
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
