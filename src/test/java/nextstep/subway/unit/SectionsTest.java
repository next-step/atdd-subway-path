package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.IllegalSectionException;


class SectionsTest {

    public static final Station 역삼역 = new Station("역삼역");
    public static final Station 선릉역 = new Station("선릉역");
    public static final Station 왕십리역 = new Station("왕십리역");
    public static final Station 잠실역 = new Station("잠실역");
    public static final Station 강남역 = new Station("강남역");
    public static final Station 삼성역 = new Station("삼성역");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(역삼역, "id", 1L);
        ReflectionTestUtils.setField(선릉역, "id", 2L);
        ReflectionTestUtils.setField(왕십리역, "id", 3L);
        ReflectionTestUtils.setField(잠실역, "id", 4L);
        ReflectionTestUtils.setField(강남역, "id", 5L);
        ReflectionTestUtils.setField(삼성역, "id", 6L);
    }
    @Test
    void testAddSection_마지막_구간의_하행_종점역이_추가하려는_구간의_상행역과_같으면_추가할_수_있다() {
        //given
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
            () -> assertThat(구간_강남_선릉.isSameWithDownStation(구간_선릉_삼성.getUpStation())).isTrue(),
            () -> assertThat(sections.getLastSection()).isEqualTo(구간_선릉_삼성)
        );
    }
    @Test
    void testAddSection_구간_중간에_역을_추가할_수_있다() {
        //given
        int existingDistance = 3;
        int newDistance = 10;
        Section 구간_강남_선릉 = new Section(강남역, 선릉역, existingDistance);
        Section 구간_강남_삼성 = new Section(강남역, 삼성역, newDistance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_강남_삼성);
        Sections sections = new Sections(existingSections);

        //when
        sections.add(구간_강남_선릉);

        //then
        List<Section> sectionList = sections.getSections();
        Section firstSection = sectionList.get(0);
        Section lastSection = sectionList.get(1);
        assertAll(
            () -> assertThat(sectionList).hasSize(2),
            () -> assertThat(firstSection.getDownStation()).isEqualTo(선릉역),
            () -> assertThat(lastSection.getUpStation()).isEqualTo(선릉역),
            () -> assertThat(lastSection.getDownStation()).isEqualTo(삼성역),
            () -> assertThat(lastSection.getDistance()).isEqualTo(newDistance - existingDistance)
        );
    }

    @Test
    void testAddSection_추가하려는_구간의_상행역과_다르면_추가할_수_없다() {
        //given
        int distance = 10;

        Section 구간_강남_선릉 = new Section(강남역, 선릉역, distance);
        Section 구간_강남_삼성 = new Section(강남역, 삼성역, distance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_강남_선릉);
        Sections sections = new Sections(existingSections);

        //when & then
        assertAll(
            () -> assertThat(구간_강남_선릉.isSameWithDownStation(구간_강남_삼성.getUpStation())).isFalse(),
            () -> assertThatThrownBy(() -> sections.add(구간_강남_삼성))
                .isInstanceOf(IllegalSectionException.class)
        );
    }

    @Test
    void testAddSection_역에_등록된_구간과_같은_구간을_등록할_수_없다() {
        //given
        int distance = 10;

        Section 구간_강남_선릉 = new Section(강남역, 선릉역, distance);
        Section 구간_강남_선릉_2 = new Section(강남역, 선릉역, distance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_강남_선릉);
        Sections sections = new Sections(existingSections);

        //when & then
        assertAll(
            () -> assertThat(구간_강남_선릉.isSameWithDownStation(구간_강남_선릉_2.getUpStation())).isFalse(),
            () -> assertThatThrownBy(() -> sections.add(구간_강남_선릉_2))
                .isInstanceOf(IllegalSectionException.class)
        );
    }

    @Test
    void testAddSection_상행_종점역을_새로_추가할_수_있다() {
        //given
        int distance = 10;

        Section 구간_역삼_선릉 = new Section(역삼역, 선릉역, distance);
        Section 구간_강남_역삼 = new Section(강남역, 역삼역, distance);

        List<Section> existingSections = new ArrayList<>();
        existingSections.add(구간_역삼_선릉);
        Sections sections = new Sections(existingSections);

        //when
        sections.add(구간_강남_역삼);

        //then
        Section firstSection = sections.getSections().get(0);
        assertThat(firstSection).isEqualTo(구간_강남_역삼);
    }

    @Test
    void testDeleteLastSection_구간이_2개_이상_등록되어_있을_떄_구간을_제거할_수_있다() {
        Section section1 = new Section(역삼역, 선릉역, 10);
        Section section2 = new Section(선릉역, 왕십리역, 2);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(section1);
        sectionList.add(section2);
        Sections sections = new Sections(sectionList);

        //when
        sections.removeSection(왕십리역);

        //then
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getLastSection().getDownStation()).isEqualTo(선릉역)
        );
    }
    @Test
    void testDeleteLastSection_노선에_구간이_1개_등록되어_있을_때_구간을_제거하면_예외를_반환한다() {
        Station upStation1 = new Station("역삼역");
        Station downStation1 = new Station("선릉역");
        Section section1 = new Section(upStation1, downStation1, 10);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(section1);
        Sections sections = new Sections(sectionList);

        //when & then
        assertThatThrownBy(
            () -> sections.removeSection(downStation1))
            .isInstanceOf(IllegalSectionException.class);
    }

    @Test
    void testDeleteLastSection_제거하려는_역이_기존_구간에_없으면_예외를_반환한다() {
        Section section1 = new Section(역삼역, 선릉역, 10);
        Section section2 = new Section(선릉역, 왕십리역, 10);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(section1);
        sectionList.add(section2);
        Sections sections = new Sections(sectionList);

        //when & then
        assertThatThrownBy(
            () -> sections.removeSection(잠실역))
            .isInstanceOf(IllegalSectionException.class);
    }

    @Test
    void testDeleteLastSection_가운데_역을_제거할_수_있다() {

        int firstSectionDistance = 10;
        Section firstSection = new Section(역삼역, 선릉역, firstSectionDistance);
        int lastSectionDistance = 12;
        Section lastSection = new Section(선릉역, 왕십리역, lastSectionDistance);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(firstSection);
        sectionList.add(lastSection);
        Sections sections = new Sections(sectionList);

        //when
        sections.removeSection(선릉역);

        //then
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(역삼역),
            () -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(왕십리역),
            () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(firstSectionDistance + lastSectionDistance)
        );
    }

    @Test
    void testDeleteLastSection_상행종점역을_제거할_수_있다() {

        int firstSectionDistance = 10;
        Section firstSection = new Section(역삼역, 선릉역, firstSectionDistance);
        int lastSectionDistance = 12;
        Section lastSection = new Section(선릉역, 왕십리역, lastSectionDistance);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(firstSection);
        sectionList.add(lastSection);
        Sections sections = new Sections(sectionList);

        //when
        sections.removeSection(역삼역);

        //then
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(선릉역),
            () -> assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(왕십리역),
            () -> assertThat(sections.getSections().get(0).getDistance()).isEqualTo(lastSectionDistance)
        );
    }

    @Test
    void testGetLastSection_마지막_구간을_반환한다() {
        //given
        Section firstSection = new Section(역삼역, 선릉역, 10);
        Section secondSection = new Section(선릉역, 왕십리역, 10);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(firstSection);
        sectionList.add(secondSection);
        Sections sections = new Sections(sectionList);

        Section lastSection = sections.getLastSection();
        assertThat(lastSection).isEqualTo(secondSection);
    }
}