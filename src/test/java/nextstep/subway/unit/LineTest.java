package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line_2 = new Line("2호선", "Green");
        Section 강남역_역삼역_구간 = new Section(line_2, 강남역, 역삼역, 10);
        line_2.getSections().add(강남역_역삼역_구간);

        Section 역삼역_선릉역_구간 = new Section(line_2, 역삼역, 선릉역, 10);

        //when
        line_2.getSections().add(역삼역_선릉역_구간);

        //then
        List<Section> sections = line_2.getSections();
        Section lastSection = sections.get(sections.size() - 1);
        assertThat(lastSection).isEqualTo(역삼역_선릉역_구간);
    }

    @Test
    @DisplayName("역 조회")
    void getStations() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line_2 = new Line("2호선", "Green");
        Section 강남역_역삼역_구간 = new Section(line_2, 강남역, 역삼역, 10);
        line_2.getSections().add(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(line_2, 역삼역, 선릉역, 10);
        line_2.getSections().add(역삼역_선릉역_구간);

        //when
        List<Station> stations = line_2.getStations();

        //then
        assertThat(stations).containsAnyOf(강남역, 역삼역, 선릉역);
    }

    @Test
    @DisplayName("구간 삭제")
    void removeSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line_2 = new Line("2호선", "Green");
        Section 강남역_역삼역_구간 = new Section(line_2, 강남역, 역삼역, 10);
        line_2.getSections().add(강남역_역삼역_구간);
        Section 역삼역_선릉역_구간 = new Section(line_2, 역삼역, 선릉역, 10);
        line_2.getSections().add(역삼역_선릉역_구간);

        //when
        line_2.removeSection(선릉역);

        //then
        assertThat(line_2.getSections()).containsAnyOf(강남역_역삼역_구간);
    }
}
