package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.domain.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @Test
    @DisplayName("구간의 하행역과 요청한 지하철 역이 다르면 True 를 리턴한다.")
    void isMissMatchDownStation() {
        final Station upStation = GANGNAM_STATION;
        final Station downStation = YEOKSAM_STATION;
        Section section = new Section(upStation, downStation, 10);

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
        Section section = new Section(GANGNAM_STATION, YEOKSAM_STATION, 10);

        // when
        assertAll(() -> {
            assertThat(section.hasStation(new Station(1L, "강남역"))).isTrue();
            assertThat(section.hasStation(new Station(2L, "역삼역"))).isTrue();
            assertThat(section.hasStation(new Station(3L, "선릉역"))).isFalse();
        });
    }
}