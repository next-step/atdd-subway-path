package nextstep.subway.domain;

import nextstep.subway.domain.exceptions.CanNotSplitSectionException;
import nextstep.subway.domain.exceptions.NotPositiveNumberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    private Line 신분당선;
    private Station 강남역;
    private Station 뱅뱅사거리역;
    private Station 양재역;
    private Station 양재시민의숲역;

    private Section 강남_양재_구간_거리10;
    private Section 강남_뱅뱅사거리_구간_거리4;
    private Section 뱅뱅사거리_양재_구간_거리6;
    private Section 양재_양재시민의숲_구간_거리10;
    

    @BeforeEach
    public void setUp() {
        신분당선 = new Line();
        강남역 = new Station(1L, "강남역");
        뱅뱅사거리역 = new Station(2L, "뱅뱅사거리역");
        양재역 = new Station(3L, "양재역");
        양재시민의숲역 = new Station(4L, "양재시민의숲역");

        강남_양재_구간_거리10 = new Section(신분당선, 강남역, 양재역, Distance.of(10));
        강남_뱅뱅사거리_구간_거리4 = new Section(신분당선, 강남역, 뱅뱅사거리역, Distance.of(4));
        뱅뱅사거리_양재_구간_거리6 = new Section(신분당선, 뱅뱅사거리역, 양재역, Distance.of(6));
        양재_양재시민의숲_구간_거리10 = new Section(신분당선, 양재역, 양재시민의숲역, Distance.of(10));
    }

    @Test
    void 구간을_생성할_수_있다() {
        assertThat(new Section(신분당선, 강남역, 양재역, Distance.of(10))).isNotNull();
    }

    @Test
    void 구간을_나눌_때_상행역이_동일하고_하행역이_다른_경우_정상적으로_나눌_수_있다() {
        // when
        Section result = 강남_양재_구간_거리10.split(강남_뱅뱅사거리_구간_거리4);

        // then
        assertThat(result.getUpStation()).isEqualTo(뱅뱅사거리역);
        assertThat(result.getDownStation()).isEqualTo(양재역);
        assertThat(result.getDistance()).isEqualTo(Distance.of(6));
    }

    @Test
    void 구간을_나눌_때_하행역이_동일하고_상행역이_다른_경우_정상적으로_나눌_수_있다() {
        // when
        Section result = 강남_양재_구간_거리10.split(뱅뱅사거리_양재_구간_거리6);

        // then
        assertThat(result.getUpStation()).isEqualTo(강남역);
        assertThat(result.getDownStation()).isEqualTo(뱅뱅사거리역);
        assertThat(result.getDistance()).isEqualTo(Distance.of(4));
    }

    @Test
    void 구간을_나눌_때_나누려는_구간의_두_역이_기존_구간에_모두_존재하는_경우_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> 강남_양재_구간_거리10.split(강남_양재_구간_거리10)).isInstanceOf(CanNotSplitSectionException.class);
    }

    @Test
    void 구간을_나눌_때_나누려는_구간의_두_역이_기존_구간에_모두_존재하지_않는_경우_예외가_발생한다() {
        // when // then
        assertThatThrownBy(() -> 강남_뱅뱅사거리_구간_거리4.split(양재_양재시민의숲_구간_거리10)).isInstanceOf(CanNotSplitSectionException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void 구간을_나눌_때_나누려는_구간의_거리가_동일하거나_큰_경우_예외가_발생한다(int distance) {
        // given
        Section 강남_뱅뱅사거리_구간 = new Section(신분당선, 강남역, 뱅뱅사거리역, Distance.of(distance));

        // when // then
        assertThatThrownBy(() -> 강남_양재_구간_거리10.split(강남_뱅뱅사거리_구간)).isInstanceOf(NotPositiveNumberException.class);
    }
}
