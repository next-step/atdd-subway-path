package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("구간 관련 기능")
class SectionTest {

    private Line line;
    private Station 강남역;
    private Station 역삼역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        this.line = new Line("2호선", "bg-red-100");
        this.강남역 = new Station("강남역");
        this.역삼역 = new Station("역삼역");
        this.정자역 = new Station("정자역");
    }

    @DisplayName("구간 생성 관련 기능")
    @Nested
    class SectionCreateTest {
        @DisplayName("구간 생성시 거리는 0이 될 수 없다.")
        @Test
        void createDistanceZero() {
            assertThatThrownBy(() -> new Section(line, 강남역, 역삼역, 0)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("구간 생성시 역은 Null이 될 수 없다.")
        @Test
        void createStationIdIsNull() {
            assertAll(
                    () -> assertThatThrownBy(() -> new Section(line, null, 역삼역, 10))
                            .isInstanceOf(IllegalArgumentException.class),
                    () -> assertThatThrownBy(() -> new Section(line, 강남역, null, 10))
                            .isInstanceOf(IllegalArgumentException.class)
            );
        }
    }

    @DisplayName("구간에 지하철이 포함여부를 반환한다.")
    @Test
    void contain() {
        Section section = new Section(line, 강남역, 역삼역, 10);

        assertAll(
                () -> assertThat(section.isContain(강남역)).isTrue(),
                () -> assertThat(section.isContain(역삼역)).isTrue(),
                () -> assertThat(section.isContain(정자역)).isFalse()
        );
    }

    @DisplayName("구간의 상행역과 역이 일치하는지 반환한다.")
    @Test
    void isUpStation() {
        Section section = new Section(line, 강남역, 역삼역, 10);

        assertAll(
                () -> assertThat(section.isUpStation(강남역)).isTrue(),
                () -> assertThat(section.isUpStation(역삼역)).isFalse()
        );
    }

    @DisplayName("구간의 하행역과 역이 일치하는지 반환한다.")
    @Test
    void isDownStation() {
        Section section = new Section(line, 강남역, 역삼역, 10);

        assertAll(
                () -> assertThat(section.isDownStation(역삼역)).isTrue(),
                () -> assertThat(section.isDownStation(강남역)).isFalse()
        );
    }

    @DisplayName("구간의 상행역을 설정한다.")
    @Test
    void setUpStation() {
        Section section = new Section(line, 강남역, 역삼역, 10);

        section.setUpStation(정자역);

        assertThat(section.getUpStation()).isEqualTo(정자역);
    }

    @DisplayName("구간의 하행역을 설정한다.")
    @Test
    void setDownStation() {
        Section section = new Section(line, 강남역, 역삼역, 10);

        section.setDownStation(정자역);

        assertThat(section.getDownStation()).isEqualTo(정자역);
    }

    @DisplayName("구간의 거리를 설정한다.")
    @Test
    void setDistance() {
        Section section = new Section(line, 강남역, 역삼역, 10);

        section.setDistance(5);

        assertThat(section.getDistance()).isEqualTo(5);
    }
}
