package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SectionTest {

    private Line line;
    private Station 중앙역;
    private Station 한대앞역;
    private Section section;

    @BeforeEach
    void setUp() {
        line = new Line("4호선", "blue");
        중앙역 = new Station("중앙역");
        한대앞역 = new Station("한대앞역");

        section = new Section(line, 중앙역, 한대앞역, 10);
    }

    @Test
    @DisplayName("상행선과 같으면 true를 다르면 false를 반환한다.")
    void equalsUpStationTest() {
        assertThat(section.equalsUpStation(중앙역)).isTrue();
        assertThat(section.equalsUpStation(한대앞역)).isFalse();
    }

    @Test
    @DisplayName("하행선과 같으면 true를 다르면 false를 반환한다.")
    void equalsDownStationTest() {
        assertThat(section.equalsDownStation(중앙역)).isFalse();
        assertThat(section.equalsDownStation(한대앞역)).isTrue();
    }

    @Test
    @DisplayName("신규 구간의 거리가 더 짧으면, 기존 구간의 거리 변경(기존 구간 거리 - 신규 구간 거리)과 기존 구간의 하행선을 신규 구간의 상행선으로 변경한다.")
    void updateDownStationToSectionUpStationTest() {
        Station 상행선 = new Station("상행선");
        section.updateDownStationToSectionUpStation(new Section(line, 상행선, new Station("하행선"), 6));

        assertAll(
                () -> assertEquals(중앙역, section.getUpStation()),
                () -> assertEquals(4, section.getDistance()),
                () -> assertEquals(상행선, section.getDownStation())
        );
    }


    @ValueSource(ints = {10, 11})
    @ParameterizedTest(name = "[{argumentsWithNames}] 신규 구간의 거리가 같거나 크면, 기존 구간의 하행선 변경에 실패한다.")
    void updateDownStationToSectionUpStationFailTest(int overDistance) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> section.updateDownStationToSectionUpStation(new Section(line, new Station("상행선"), new Station("하행선"), overDistance)));
    }

    @Test
    @DisplayName("신규 구간의 거리가 더 짧으면, 기존 구간의 거리 변경(기존 구간 거리 - 신규 구간 거리)과 기존 구간의 상행선을 신규 구간의 하행선으로 변경한다.")
    void updateUpStationToSectionDownStationTest() {
        Station 하행선 = new Station("하행선");
        section.updateUpStationToSectionDownStation(new Section(line, new Station("상행선"), 하행선, 6));

        assertAll(
                () -> assertEquals(하행선, section.getUpStation()),
                () -> assertEquals(4, section.getDistance()),
                () -> assertEquals(한대앞역, section.getDownStation())
        );
    }

    @ValueSource(ints = {10, 11})
    @ParameterizedTest(name = "[{argumentsWithNames}] 신규 구간의 거리가 같거나 크면, 기존 구간의 상행선 변경에 실패한다.")
    void updateUpStationToSectionDownStationFailTest(int overDistance) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> section.updateUpStationToSectionDownStation(new Section(line, new Station("상행선"), new Station("하행선"), overDistance)));
    }

}