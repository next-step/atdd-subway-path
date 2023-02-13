package nextstep.subway.domain;

import nextstep.subway.domain.exceptions.CanNotAddSectionException;
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
    private Section 뱅뱅사거리_양재_구간;
    private Section 양재_양재시민의숲_구간;

    @BeforeEach
    public void setUp() {
        강남역 = new Station(1L, "강남역");
        뱅뱅사거리역 = new Station(2L, "뱅뱅사거리역");
        양재역 = new Station(3L, "양재역");
        양재시민의숲역 = new Station(4L, "양재시민의숲역");

        강남_양재_구간 = new Section(null, 강남역, 양재역, Distance.of(10));
        강남_뱅뱅사거리_구간 = new Section(null, 강남역, 뱅뱅사거리역, Distance.of(5));
        뱅뱅사거리_양재_구간 = new Section(null, 뱅뱅사거리역, 양재역, Distance.of(5));
        양재_양재시민의숲_구간 = new Section(null, 양재역, 양재시민의숲역, Distance.of(10));
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
    void 구간들에_이미_존재하는_상행역과_새로운_하행역인_구간을_추가하는_경우_정상적으로_추가된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);

        // when
        sections.addSection(강남_뱅뱅사거리_구간);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 뱅뱅사거리역, 양재역);
    }

    @Test
    void 구간들에_새로운_상행역과_이미_존재하는_하행역인_구간을_추가하는_경우_정상적으로_추가된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);

        // when
        sections.addSection(뱅뱅사거리_양재_구간);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 뱅뱅사거리역, 양재역);
    }

    @Test
    void 구간들에_상행역과_하행역_모두_이미_등록되어_있는_경우_구간_추가_시_예외가_발생한다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);
        sections.addSection(양재_양재시민의숲_구간);

        Section 강남_양재시민의숲_구간 = new Section(null, 강남역, 양재시민의숲역, Distance.of(15));

        // when // then
        assertThatThrownBy(() -> sections.addSection(강남_양재시민의숲_구간))
                .isInstanceOf(CanNotAddSectionException.class)
                .hasMessage("상행역과 하행역 모두 이미 존재하여 구간 추가 불가능");
    }

    @Test
    void 구간들에_상행역과_하행역_모두_등록되어_있지_않는_경우_구간_추가_시_예외가_발생한다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_뱅뱅사거리_구간);

        // when // then
        assertThatThrownBy(() -> sections.addSection(양재_양재시민의숲_구간))
                .isInstanceOf(CanNotAddSectionException.class)
                .hasMessage("상행역과 하행역 모두 존재하지 않아 구간 추가 불가능");
    }

    @Test
    void 구간들에_속한_역들을_조회하는_경우_상행역에서_하행역_순서로_조회된다() {
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

    @Test
    void 구간들에서_하행종점역과_상행종점역이_아닌_역을_제거하는_경우_정상적으로_제거된다() {
        // given
        Sections sections = new Sections();
        sections.addSection(강남_양재_구간);
        sections.addSection(양재_양재시민의숲_구간);

        // when
        sections.remove(양재역);

        // then 
        assertThat(sections.getStations()).containsExactly(강남역, 양재시민의숲역);
    }
}
