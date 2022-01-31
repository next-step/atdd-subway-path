package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    private static final Line line = new Line("2호선", "bg-green-600");
    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 선릉역 = new Station("선릉역");

    @Test
    @DisplayName("마지막 하행역 조회")
    void getLastDownStation() {
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 10);
        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        Station lastDownStation = sections.getLastDownStation();

        assertThat(lastDownStation).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("구간의 모든 역 조회")
    void getAllStations() {
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 10);
        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        List<Station> stations = sections.getAllStations();

        assertThat(stations).hasSize(3);
    }

    @Test
    @DisplayName("하행역에 해당하는 구간 조회")
    void getByDownStation() {
        Section section1 = new Section(line, 강남역, 역삼역, 10);
        Section section2 = new Section(line, 역삼역, 선릉역, 10);
        Sections sections = new Sections();
        sections.add(section1);
        sections.add(section2);

        Section section = sections.getByDownStation(역삼역);
        assertThat(section).isEqualTo(section1);
    }
}