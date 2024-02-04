package nextstep.subway.line.repository.domain;

import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.station.repository.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SectionTest {
    private static final long SECTION_ID = 1L;
    private Station 강남역;
    private Station 선릉역;
    private int sectionDistance;
    private Section section;

    @BeforeEach
    void setUp() {
        강남역 = StationFactory.createStation(1L, "강남역");
        선릉역 = StationFactory.createStation(2L, "선릉역");
        sectionDistance = 10;
        section = SectionFactory.createSection(SECTION_ID, 강남역, 선릉역, sectionDistance);
    }

    @Test
    @DisplayName("Section 을 생성할 수 있다.")
    void sectionTest() {
        assertSoftly(softly -> {
            softly.assertThat(section.getId()).isEqualTo(SECTION_ID);
            softly.assertThat(section.getUpStation()).isEqualTo(강남역);
            softly.assertThat(section.getDownStation()).isEqualTo(선릉역);
            softly.assertThat(section.getDistance()).isEqualTo(sectionDistance);
        });
    }

    @Test
    @DisplayName("Section 클래스의 id 가 같다면 동등한 객체이다.")
    void sectionEqualsTest() {
        final Section newSection = SectionFactory.createSection(SECTION_ID, 강남역, 선릉역, sectionDistance);

        assertThat(section).isEqualTo(newSection);
    }

    @Test
    @DisplayName("Section 의 contains 메서드를 통해 해당 station 을 포함하고 있는지 알 수 있다.")
    void sectionContainsTest() {
        final Station 역삼역 = StationFactory.createStation(3L, "역삼역");

        assertSoftly(softly -> {
            softly.assertThat(section.contains(강남역)).isTrue();
            softly.assertThat(section.contains(선릉역)).isTrue();
            softly.assertThat(section.contains(역삼역)).isFalse();
        });
    }
}
