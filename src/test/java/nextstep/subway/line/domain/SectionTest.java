package nextstep.subway.line.domain;

import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SectionTest {
    private static final long SECTION_ID = 1L;
    private Station 강남역;
    private Station 선릉역;
    private Station 역삼역;
    private int 강남역_선릉역_구간_길이;
    private Section 강남역_선릉역_구간;

    @BeforeEach
    void setUp() {
        강남역 = StationFactory.createStation(1L, "강남역");
        선릉역 = StationFactory.createStation(2L, "선릉역");
        역삼역 = StationFactory.createStation(3L, "역삼역");
        강남역_선릉역_구간_길이 = 10;
        강남역_선릉역_구간 = SectionFactory.createSection(SECTION_ID, 강남역, 선릉역, 강남역_선릉역_구간_길이);
    }

    @Test
    @DisplayName("Section 을 생성할 수 있다.")
    void sectionTest() {
        assertSoftly(softly -> {
            softly.assertThat(강남역_선릉역_구간.getId()).isEqualTo(SECTION_ID);
            softly.assertThat(강남역_선릉역_구간.getUpStation()).isEqualTo(강남역);
            softly.assertThat(강남역_선릉역_구간.getDownStation()).isEqualTo(선릉역);
            softly.assertThat(강남역_선릉역_구간.getDistance()).isEqualTo(강남역_선릉역_구간_길이);
        });
    }

    @Test
    @DisplayName("Section 클래스의 id 가 같다면 동등한 객체이다.")
    void sectionEqualsTest() {
        final Section newSection = SectionFactory.createSection(SECTION_ID, 강남역, 선릉역, 강남역_선릉역_구간_길이);

        assertThat(강남역_선릉역_구간).isEqualTo(newSection);
    }

    @Test
    @DisplayName("Section 의 contains 메서드를 통해 해당 station 을 포함하고 있는지 알 수 있다.")
    void sectionContainsTest() {
        final Station 역삼역 = StationFactory.createStation(3L, "역삼역");

        assertSoftly(softly -> {
            softly.assertThat(강남역_선릉역_구간.contains(강남역)).isTrue();
            softly.assertThat(강남역_선릉역_구간.contains(선릉역)).isTrue();
            softly.assertThat(강남역_선릉역_구간.contains(역삼역)).isFalse();
        });
    }

    @Test
    @DisplayName("Section 의 reconnect 메서드를 통해 upStation 과 distance 를 수정할 수 있다.")
    void reconnectTest() {
        final Section 강남역_역삼역_구간 = SectionFactory.createSection(SECTION_ID, 강남역, 역삼역, 5);
        강남역_선릉역_구간.reconnect(강남역_역삼역_구간);

        assertSoftly(softly -> {
            softly.assertThat(강남역_선릉역_구간.getUpStation()).isEqualTo(역삼역);
            softly.assertThat(강남역_선릉역_구간.getDistance()).isEqualTo(강남역_선릉역_구간_길이 - 강남역_역삼역_구간.getDistance());
        });
    }
}
