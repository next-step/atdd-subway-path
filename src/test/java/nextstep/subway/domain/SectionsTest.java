package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("모든 구간의 지하철 역들을 반환한다.")
    void getStations() {
        //given
        Line line = new Line("2호선", "bg-green-600");

        Station gangnam = new Station("강남역");
        Station yeoksam = new Station("역삼역");
        Station seolleung = new Station("선릉역");
        final int distance = 10;

        Section section = new Section(line, gangnam, yeoksam, distance);
        Section section2 = new Section(line, yeoksam, seolleung, distance);

        Sections sections = new Sections();
        sections.add(section);
        sections.add(section2);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsOnly(gangnam, yeoksam, seolleung);
    }

}