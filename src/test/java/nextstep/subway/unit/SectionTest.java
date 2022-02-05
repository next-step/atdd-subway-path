package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    private Line 신분당선;
    private Station 강남역;
    private Station 정자역;
    private Station 판교역;
    private int distance;
    private int newDistance;
    private Section section;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
        판교역 = new Station("판교역");
        distance = 10;
        newDistance = 5;
        section = new Section(신분당선, 강남역, 정자역, distance);
    }

    @DisplayName("Section의 상행역과 구간거리를 변경하는 테스트")
    @Test
    void changeUpStation() {
        // given
        Section newSection = new Section(신분당선, 강남역, 판교역, newDistance);

        // when
        Section changedSection = section.changeUpStation(newSection);

        // then
        assertThat(changedSection.getUpStation()).isEqualTo(판교역);
        assertThat(changedSection.getDistance()).isEqualTo(newDistance);
    }

    @DisplayName("Section의 상행역과 구간거리를 변경하는 테스트 - 구간 거리가 같음")
    @Test
    void changeUpStationFail() {
        // given
        int newDistance = distance;
        Section newSection = new Section(신분당선, 강남역, 판교역, newDistance);

        // when & then
        assertThatThrownBy(() -> section.changeUpStation(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Section의 하행역과 구간거리를 변경하는 테스트")
    @Test
    void changeDownStation() {
        // given
        Section newSection = new Section(신분당선, 판교역, 정자역, newDistance);

        // when
        Section changedSection = section.changeDownStation(newSection);

        // then
        assertThat(changedSection.getDownStation()).isEqualTo(판교역);
        assertThat(changedSection.getDistance()).isEqualTo(newDistance);
    }

    @DisplayName("Section의 하행역과 구간거리를 변경 실패 - 구간 거리가 같음")
    @Test
    void changeDownStationFail() {
        // given
        int newDistance = distance;
        Section newSection = new Section(신분당선, 판교역, 정자역, newDistance);

        // when & then
        assertThatThrownBy(() -> section.changeDownStation(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }
}