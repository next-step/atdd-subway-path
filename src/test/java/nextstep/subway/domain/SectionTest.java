package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.domain.factory.SectionFactory.createSection;
import static nextstep.subway.domain.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @Test
    @DisplayName("구간의 하행역과 요청한 지하철 역이 다르면 True 를 리턴한다.")
    void isMissMatchDownStation() {
        final Station upStation = GANGNAM_STATION;
        final Station downStation = YEOKSAM_STATION;
        Section section = createSection(upStation, downStation, 10);

        assertAll(() -> {
            assertThat(section.isMissMatchDownStation(new Station(1L, "강남역"))).isTrue();
            assertThat(section.isMissMatchDownStation(new Station(3L, "선릉역"))).isTrue();
            assertThat(section.isMissMatchDownStation(new Station(2L, "역삼역"))).isFalse();
        });
    }

    @Test
    @DisplayName("상행역 혹은 하행역 중에 동일한 역이 있으면 True, 없으면 False 를 리턴한다.")
    void hasStations() {
        // given
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        // when
        assertAll(() -> {
            assertThat(section.hasStation(new Station(1L, "강남역"))).isTrue();
            assertThat(section.hasStation(new Station(2L, "역삼역"))).isTrue();
            assertThat(section.hasStation(new Station(3L, "선릉역"))).isFalse();
        });
    }

    @Test
    @DisplayName("구간 간에 같은 역이 있으면 연결 가능한 구간이다.")
    void isConnectable() {
        // given
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        // when
        Section connectableSection = createSection(SEOLLEUNG_STATION, YEOKSAM_STATION, 10);
        Section notConnectableSection = createSection(SAMSUNG_STATION, SEOLLEUNG_STATION, 10);

        // then
        assertAll(() -> {
            assertThat(section.isConnectable(connectableSection)).isTrue();
            assertThat(section.isConnectable(notConnectableSection)).isFalse();
        });
    }

    @Test
    @DisplayName("구간 사이에 구간을 추가하려면 상행역끼리 같거나 하행역끼리 같아야한다. ")
    void invalid_connectInside() {
        // given
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        Section additionalSection = createSection(YEOKSAM_STATION, SEOLLEUNG_STATION, 3);

        // when
        assertThatThrownBy(() -> section.connectInside(additionalSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역끼리 같거나 하행역끼리 같으면 내부 결합을 할 수 있다.")
    void isConnectInside() {
        // given
        Section sameUpStationSection_1 = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        Section sameUpStationSection_2 = createSection(GANGNAM_STATION, SEOLLEUNG_STATION, 3);

        Section sameDownStationSection_1 = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        Section sameDownStationSection_2 = createSection(SEOLLEUNG_STATION, YEOKSAM_STATION, 3);

        Section differentStationSection_1 = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        Section differentStationSection_2 = createSection(SEOLLEUNG_STATION, SAMSUNG_STATION, 3);

        // then
        assertAll(() -> {
            assertThat(sameUpStationSection_1.isConnectInSide(sameUpStationSection_2)).isTrue();
            assertThat(sameDownStationSection_1.isConnectInSide(sameDownStationSection_2)).isTrue();
            assertThat(differentStationSection_1.isConnectInSide(differentStationSection_2)).isFalse();
        });
    }

    @Test
    @DisplayName("상행역 기준으로 구간 사이에 구간을 추가하면 상행역과 거리가 변경된다.")
    void connectInside_upStation() {
        // given
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        // when
        Section additionalSection = createSection(GANGNAM_STATION, SEOLLEUNG_STATION, 3);
        section.connectInside(additionalSection);

        // then
        assertAll(() -> {
            assertThat(section.getUpStation()).isNotSameAs(GANGNAM_STATION);
            assertThat(section.getUpStation()).isEqualTo(SEOLLEUNG_STATION);
            assertThat(section.getDistance()).isEqualTo(new Distance(7));
        });
    }

    @Test
    @DisplayName("하행역 기준으로 구간 사이에 구간을 추가하면 하행역과 거리가 변경된다.")
    void connectInside_downStation() {
        // given
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        // when
        Section additionalSection = createSection(SEOLLEUNG_STATION, YEOKSAM_STATION, 3);
        section.connectInside(additionalSection);

        // then
        assertAll(() -> {
            assertThat(section.getDownStation()).isNotSameAs(YEOKSAM_STATION);
            assertThat(section.getDownStation()).isEqualTo(SEOLLEUNG_STATION);
            assertThat(section.getDistance()).isEqualTo(new Distance(7));
        });
    }
}