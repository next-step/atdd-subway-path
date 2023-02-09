package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Station 강남역;
    private Station 뱅뱅사거리역;
    private Station 양재역;
    private Station 양재시민의숲역;

    private Section 강남_양재_구간;
    private Section 강남_뱅뱅사거리_구간;
    private Section 양재_양재시민의숲_구간;

    @BeforeEach
    public void setUp() {
        강남역 = new Station(1L, "강남역");
        뱅뱅사거리역 = new Station(2L, "뱅뱅사거리역");
        양재역 = new Station(3L, "양재역");
        양재시민의숲역 = new Station(4L, "양재시민의숲역");

        강남_양재_구간 = new Section(null, 강남역, 양재역, 10);
        강남_뱅뱅사거리_구간 = new Section(null, 강남역, 뱅뱅사거리역, 5);
        양재_양재시민의숲_구간 = new Section(null, 양재역, 양재시민의숲역, 10);
    }

    @Test
    void 구간들을_정상적으로_생성한다() {
        assertThat(new Sections()).isNotNull();
    }

    @Test
    void 구간의_끝에_새로운_구간을_추가하는_경우_정상적으로_추가된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);

        // when
        sections.addSection(양재_양재시민의숲_구간);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 양재역, 양재시민의숲역);
    }

    @Test
    void 구간의_앞에_새로운_구간을_추가하는_경우_정상적으로_추가된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(양재_양재시민의숲_구간);

        // when
        sections.addSection(강남_양재_구간);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 양재역, 양재시민의숲역);
    }

    @Test
    void 구간의_사이에_새로운_구간을_추가하는_경우_정상적으로_추가된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);

        // when
        sections.addSection(강남_뱅뱅사거리_구간);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 뱅뱅사거리역, 양재역);
    }

    @Test
    void 구간들에_상행역과_하행역_모두_이미_등록되어_있는_경우_노선_추가_시_예외가_발생한다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);
        sections.addSection(양재_양재시민의숲_구간);

        Section 강남_양재시민의숲_구간 = new Section(null, 강남역, 양재시민의숲역, 15);

        // when // then
        assertThatThrownBy(() -> sections.addSection(강남_양재시민의숲_구간)).isInstanceOf(AssertionError.class);
    }

    @Test
    void 구간들에_상행역과_하행역_모두_등록되어_있지_않는_경우_노선_추가_시_예외가_발생한다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_뱅뱅사거리_구간);

        // when // then
        assertThatThrownBy(() -> sections.addSection(양재_양재시민의숲_구간)).isInstanceOf(AssertionError.class);
    }

    @Test
    void 구간들에_속한_역들을_조회하는_경우_상행선에서_하행선_순서로_조회된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);
        sections.addSection(강남_뱅뱅사거리_구간);
        sections.addSection(양재_양재시민의숲_구간);

        // when
        List<Station> result = sections.getStations();

        // then
        assertThat(result).containsExactly(강남역, 뱅뱅사거리역, 양재역, 양재시민의숲역);
    }

    @Test
    void 구간들에서_마지막_하행선을_제거하는_경우_정상적으로_제거된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);
        sections.addSection(양재_양재시민의숲_구간);

        // when
        sections.remove(양재시민의숲역);

        // then 
        assertThat(sections.getStations()).containsExactly(강남역, 양재역);
    }
}
