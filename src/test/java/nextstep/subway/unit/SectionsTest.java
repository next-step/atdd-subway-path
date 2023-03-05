package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class SectionsTest {

    Sections sections;
    Station 서울숲역;
    Station 강남구청역;
    Station 압구정로데오역;
    Line 수인분당선;
    final int DISTANCE = 6;
    final int LONGER_DISTANCE = 10;

    @BeforeEach
    void setUp() {
        sections = new Sections();

        강남구청역 = new Station("강남구청역");
        압구정로데오역 = new Station("압구정로데오역");
        서울숲역 = new Station("서울숲역");

        수인분당선 = new Line("수인분당선", "bg-yellow-600");

        sections.add(수인분당선, 강남구청역, 압구정로데오역, LONGER_DISTANCE);

    }

    @Test
    void add() {
        sections.add(수인분당선, 강남구청역, 서울숲역, DISTANCE);

        assertThat(sections.getSections().size()).isEqualTo(2);
        assertThat(sections.getStations()).containsExactly(강남구청역, 서울숲역, 압구정로데오역);
    }

    @Test
    void getStations() {
        Station 선정릉역 = new Station("선정릉");
        sections.add(수인분당선, 강남구청역, 서울숲역, DISTANCE);
        sections.add(수인분당선, 선정릉역, 강남구청역, LONGER_DISTANCE);

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(선정릉역, 강남구청역, 서울숲역, 압구정로데오역);
    }

    @Test
    @DisplayName("구간 제거 - 마지막역")
    void removeLast() {
        Station 선정릉역 = new Station("선정릉");
        sections.add(수인분당선, 강남구청역, 서울숲역, DISTANCE);
        sections.add(수인분당선, 선정릉역, 강남구청역, LONGER_DISTANCE);

        sections.remove(압구정로데오역);

        assertThat(sections.getSections().size()).isEqualTo(2);
        assertThat(sections.getStations()).containsExactly(선정릉역, 강남구청역, 서울숲역);
    }

    @Test
    @DisplayName("구간 제거 - 첫번째역")
    void removeFirst() {
        Station 선정릉역 = new Station("선정릉");
        sections.add(수인분당선, 강남구청역, 서울숲역, DISTANCE);
        sections.add(수인분당선, 선정릉역, 강남구청역, LONGER_DISTANCE);

        sections.remove(선정릉역);

        assertThat(sections.getSections().size()).isEqualTo(2);
        assertThat(sections.getStations()).containsExactly(강남구청역, 서울숲역, 압구정로데오역);
    }

    @Test
    @DisplayName("구간 제거 - 중간역")
    void removeMiddle() {
        sections.add(수인분당선, 서울숲역, 강남구청역, DISTANCE);

        sections.remove(강남구청역);

        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getSections().get(0).getDistance())
            .isEqualTo(DISTANCE + LONGER_DISTANCE);
        assertThat(sections.getStations()).containsExactly(서울숲역, 압구정로데오역);
    }

    @Test
    @DisplayName("구간 제거 실패 - 2개 미만의 구간")
    void removeWhenSectionIsOnlyOne() {
        assertThatThrownBy(() -> sections.remove(강남구청역))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 제거 실패 - 구간에 없는 역")
    void removeStationThatDoesNotExistInSections() {
        Station 선정릉역 = new Station("선정릉");
        sections.add(수인분당선, 강남구청역, 서울숲역, DISTANCE);

        assertThatThrownBy(() -> sections.remove(선정릉역))
            .isInstanceOf(EntityNotFoundException.class);
    }

}
