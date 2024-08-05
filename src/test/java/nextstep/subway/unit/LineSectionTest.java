package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LineSectionTest {

    private Line 신분당선;
    private Station 신사역 = new Station("신사역");
    private Station 논현역 = new Station("논현역");
    private Station 신논현역 = new Station("신논현역");

    private LineSection lineSection;


    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "bg-red-600", new LineSections());
        lineSection = new LineSection(신분당선, 신사역, 논현역, 10L);
    }

    @DisplayName("LineSection 생성 테스트")
    @Test
    void createLineSection() {

        // when, then
        assertThat(lineSection.getLine()).isEqualTo(신분당선);
        assertThat(lineSection.getUpStation()).isEqualTo(신사역);
        assertThat(lineSection.getDownStation()).isEqualTo(논현역);
        assertThat(lineSection.getDistance()).isEqualTo(10L);
    }

    @DisplayName("hasSameUpStation 테스트")
    @Nested
    class HasSameUpStationTest {

        @Test
        @DisplayName("동일한 상행역을 가진 경우 true를 반환한다")
        void returnsTrueForSameUpStation() {
            // when, then
            assertThat(lineSection.hasSameUpStation(신사역)).isTrue();
        }

        @Test
        @DisplayName("다른 상행역을 가진 경우 false를 반환한다")
        void returnsFalseForDifferentUpStation() {
            // when, then
            assertThat(lineSection.hasSameUpStation(신논현역)).isFalse();
        }
    }

    @DisplayName("updateDownStationAndDistance 테스트")
    @Test
    void updateDownStationAndDistance() {
        // when
        lineSection.updateDownStationAndDistance(신논현역, 15L);

        // then
        assertThat(lineSection.getDownStation()).isEqualTo(신논현역);
        assertThat(lineSection.getDistance()).isEqualTo(15L);
    }
}
