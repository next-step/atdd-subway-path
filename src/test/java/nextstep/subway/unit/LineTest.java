package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @Test
    void addSection() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");

        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);


        // then
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        final Section 역삼역_선릉역_구간 = new Section(이호선, 역삼역, 선릉역, 10);
        assertThat(이호선.getSections()).containsAnyOf(강남역_역삼역_구간, 역삼역_선릉역_구간);
    }

    @Test
    void getStations() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
        이호선.addSection(강남역, 역삼역, 10);

        // when
        final List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsAnyOf(강남역, 역삼역);
    }

    @Test
    void removeSection() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Line 이호선 = new Line("2호선", "bg-green-600");
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when
        이호선.removeLastSection(선릉역);

        // then
        final List<Station> stations = 이호선.getStations();
        assertThat(stations).containsOnly(강남역, 역삼역);
    }
}
