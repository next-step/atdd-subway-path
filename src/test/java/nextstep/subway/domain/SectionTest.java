package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 도메인 테스트")
class SectionTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Station 미금역;
    private Line 신분당선;
    private Section 강남역_판교역;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        미금역 = Station.of("미금역");
        신분당선 = new Line();
        강남역_판교역 = Section.of(신분당선, 강남역, 정자역, 40);
    }

    @DisplayName("구간이 상행역을 포함 하고 있으면 true를 반환한다")
    @Test
    void isContainSection_up() {
        // given
        Section 강남역_정자역 = Section.of(신분당선, 강남역, 정자역, 40);

        // when
        boolean containStation = 강남역_판교역.isContainSection(강남역_정자역);

        // then
        assertThat(containStation).isTrue();
    }

    @DisplayName("구간이 하행역을 포함 하고 있으면 true를 반환한다")
    @Test
    void isContainSection_down() {
        // given
        Section 판교역_정자역 = Section.of(신분당선, 판교역, 정자역, 40);

        // when
        boolean containStation = 강남역_판교역.isContainSection(판교역_정자역);

        // then
        assertThat(containStation).isTrue();
    }

    @DisplayName("구간이 역을 포함 하고 있지 않으면 false를 반환한다")
    @Test
    void isContainSection_false() {
        // given
        Section 정자역_미금역 = Section.of(신분당선, 정자역, 미금역, 40);

        // when
        boolean containStation = 강남역_판교역.isContainSection(정자역_미금역);

        // then
        assertThat(containStation).isTrue();
    }

}