package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    Sections sections;
    Station 서울숲역;
    Station 강남구청역;
    Station 압구정로데오역;
    Line 수인분당선;
    Section 강남구청역_압구정로데오역_구간;
    Section 강남구청역_서울숲역_구간;
    int distance = 6;

    @BeforeEach
    void setUp() {
        sections = new Sections();

        강남구청역 = new Station("강남구청역");
        압구정로데오역 = new Station("압구정로데오역");
        서울숲역 = new Station("서울숲역");

        수인분당선 = new Line("수인분당선", "bg-yellow-600");

        강남구청역_압구정로데오역_구간 = new Section(수인분당선, 강남구청역, 압구정로데오역, 10);
        강남구청역_서울숲역_구간 = new Section(수인분당선, 강남구청역, 서울숲역, distance);
        sections.add(강남구청역_압구정로데오역_구간);

    }

    @Test
    void add() {
        sections.add(강남구청역_서울숲역_구간);

        assertThat(sections.getSections().size()).isEqualTo(2);
        assertThat(sections.getStations()).containsExactly(강남구청역, 서울숲역, 압구정로데오역);
    }

    @Test
    void getStations() {
        Station 선정릉역 = new Station("선정릉");
        Section 선정릉역_강남구청역_구간 = new Section(수인분당선, 선정릉역, 강남구청역, 10);
        sections.add(강남구청역_서울숲역_구간);
        sections.add(선정릉역_강남구청역_구간);

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(선정릉역, 강남구청역, 서울숲역, 압구정로데오역);
    }

}
