package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.VO.SectionsVO;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {
    private static final String LINE_ONE = "1호선";
    private static final String BACKGROUND_COLOR_BLUE = "bg-color-blue";
    private static final String SEOUL_STATION = "서울역";
    private static final String YONGSAN_STATION = "용산역";
    private static final String HONGDAE_STATION = "홍대역";
    private static final String SONGPA_STATION = "송파역";
    private static final int DISTANCE_TEN = 10;
    private static final int DISTANCE_FIVE = 5;

    private Line lineOne;
    private Station seoulStation;
    private Station yongSanStation;
    private Station hongDaeStation;

    @BeforeEach
    void setUp() {
        lineOne = new Line(LINE_ONE, BACKGROUND_COLOR_BLUE);
        seoulStation = new Station(SEOUL_STATION);
        yongSanStation = new Station(YONGSAN_STATION);
        hongDaeStation = new Station(HONGDAE_STATION);
    }

    @DisplayName("구간의 일급 컬렉션인 Sections에 구간을 추가할 수 있다")
    @Test
    void addSectionWithSizeZero() {
        //given
        Section section = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(section);

        //then
        Assertions.assertThat(sections.getSections()).containsExactly(section);
    }

    @DisplayName("기존 구간의 상행선에서 출발하는 구간을 생성하고 이를 Sections에 삽입하면 수정된 구간들을 확인할 수 있다")
    @Test
    void addSection() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, seoulStation, hongDaeStation, DISTANCE_FIVE);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);
        sections.addSection(sectionTwo);

        Section modifiedSectionOne = new Section(sectionTwo.getLine(), sectionOne.getUpStation(), sectionTwo.getDownStation(), sectionTwo.getDistance());
        Section modifiedSectionTwo = new Section(sectionOne.getLine(), sectionTwo.getDownStation(), sectionOne.getDownStation(), sectionOne.getDistance() - sectionTwo.getDistance());

        //then
        Assertions.assertThat(sections.getSections()).containsExactly(modifiedSectionOne, modifiedSectionTwo);
    }

    @DisplayName("추가하는 구간이 기존의 구간의 역 사이의 길이보다 크거나 같으면 InvalidValueException이 발생한다")
    @Test
    void addSectionWithInvalidValueException() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_FIVE);
        Section sectionTwo = new Section(lineOne, seoulStation, hongDaeStation, DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);

        //then
        Assertions.assertThatThrownBy(() -> {
                    sections.addSection(sectionTwo);
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH.getErrorMessage());
    }

    @DisplayName("노선의 마지막 구간의 지하철역 객체로 구간을 삭제할 수 있다")
    @Test
    void removeSection() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);
        sections.addSection(sectionTwo);

        //then
        sections.removeSection(sectionTwo.getUpStation());
        Assertions.assertThat(sections.getSections()).containsExactly(sectionOne);
        Assertions.assertThat(sections.getSections()).doesNotContain(sectionTwo);
    }

    @DisplayName("노선의 마지막 구간의 지하철역이 아닌 객체로 구간을 삭제하면 InvalidValueException이 발생한다")
    @Test
    void removeSectionWithException() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);
        sections.addSection(sectionTwo);

        //then
        Assertions.assertThatThrownBy(() -> {
                    sections.removeSection(sectionOne.getUpStation());
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.NOT_STATION_OF_END_SECTION.getErrorMessage());
    }

    @DisplayName("데이터베이스로 조회된 정렬되지 않는 Section 리스트를 정리해서 VO 객체로 반환한다")
    @Test
    void sort() {
        //given
        Station songPaStation = new Station(SONGPA_STATION);
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DISTANCE_TEN);
        Section sectionThree = new Section(lineOne, hongDaeStation, songPaStation, DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionTwo);
        sections.addSection(sectionThree);
        sections.addSection(sectionOne);

        //then
        SectionsVO sortedSections = sections.sort();
        Assertions.assertThat(sortedSections.getSections()).containsExactly(sectionOne, sectionTwo, sectionThree);
    }
}
