package nextstep.subway.station.repository.domain;

import nextstep.util.ReflectionUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class StationTest {
    private static final long 강남역_ID = 1L;
    Station 강남역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        ReflectionUtils.injectIdField(강남역, 강남역_ID);
    }

    @Test
    @DisplayName("Station 클래스를 생성할 수 있다.")
    void createStationTest() {
        assertSoftly(softly->{
            softly.assertThat(강남역.getId()).isEqualTo(강남역_ID);
            softly.assertThat(강남역.getName()).isEqualTo("강남역");
        });
    }

    @Test
    @DisplayName("Station 클래스의 id 가 같다면 동등한 객체이다.")
    void stationEqualsTest() {
        final Station 선릉역 = new Station("선릉역");

        ReflectionUtils.injectIdField(선릉역, 강남역_ID);

        assertThat(강남역).isEqualTo(선릉역);
    }

    @Test
    @DisplayName("Station 클래스의 id 가 다르다면 동등하지 않은 객체이다.")
    void stationNotEqualsTest() {
        final Station 선릉역 = new Station("선릉역");

        ReflectionUtils.injectIdField(선릉역, 강남역_ID + 1);

        assertThat(강남역.notEquals(선릉역)).isTrue();
    }

}
