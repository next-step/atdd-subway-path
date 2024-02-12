package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.IllegalSectionException;


class SectionsTest {

    @Test
    void test_마지막_구간의_하행_종점역이_추가하려는_구간의_상행역과_같으면_추가할_수_있다() {
        //given
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 삼성역 = new Station("삼성역");
        int distance = 10;

        Section 구간_강남_선릉 = new Section(강남역, 선릉역, distance);
        Section 구간_선릉_삼성 = new Section(선릉역, 삼성역, distance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_강남_선릉);
        Sections sections = new Sections(existingSections);
        //when
        sections.add(구간_선릉_삼성);

        //then
        assertAll(
            () -> assertThat(구간_강남_선릉.isPossibleToAdd(구간_선릉_삼성.getUpStation())).isTrue(),
            () -> assertThat(sections.getLastSection()).isEqualTo(구간_선릉_삼성)
        );
    }

    @Test
    void test_마지막_구간의_하행_종점역이_추가하려는_구간의_상행역과_다르면_추가할_수_없다() {
        //given
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 삼성역 = new Station("삼성역");
        int distance = 10;

        Section 구간_강남_선릉 = new Section(강남역, 선릉역, distance);
        Section 구간_강남_삼성 = new Section(강남역, 삼성역, distance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_강남_선릉);
        Sections sections = new Sections(existingSections);

        //when & then
        assertAll(
            () -> assertThat(구간_강남_선릉.isPossibleToAdd(구간_강남_삼성.getUpStation())).isFalse(),
            () -> assertThatThrownBy(() -> sections.add(구간_강남_삼성))
                .isInstanceOf(IllegalSectionException.class)
        );
    }

    @Test
    void test_마지막_구간의_하행_종점역이_추가하려는_구간의_상행역과_다르면_추가할_수_없다_2() {
        //given
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        int distance = 10;

        Section 구간_강남_선릉 = new Section(강남역, 선릉역, distance);
        Section 구간_강남_선릉_2 = new Section(강남역, 선릉역, distance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_강남_선릉);
        Sections sections = new Sections(existingSections);

        //when & then
        assertAll(
            () -> assertThat(구간_강남_선릉.isPossibleToAdd(구간_강남_선릉_2.getUpStation())).isFalse(),
            () -> assertThatThrownBy(() -> sections.add(구간_강남_선릉_2))
                .isInstanceOf(IllegalSectionException.class)
        );
    }
}