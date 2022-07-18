package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.domain.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @Test
    @DisplayName("구간의 하행역과 요청한 지하철 역이 같으면 True 를 리턴한다.")
    void isMatchDownStation() {
        final Station upStation = GANGNAM_STATION;
        final Station downStation = YEOKSAM_STATION;
        Section section = new Section(upStation, downStation, 10);

        assertAll(() -> {
            assertThat(section.isMatchDownStation(new Station(1L, "강남역"))).isFalse();
            assertThat(section.isMatchDownStation(new Station(3L, "선릉역"))).isFalse();
            assertThat(section.isMatchDownStation(new Station(2L, "역삼역"))).isTrue();
        });
    }

}