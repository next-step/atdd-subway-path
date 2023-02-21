package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.fixture.DistanceFixtures;
import nextstep.subway.fixture.StationFixtures;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SectionsTest {
    private static final String LINE_ONE = "1호선";
    private static final String BACKGROUND_COLOR_BLUE = "bg-color-blue";

    private Line lineOne;
    private Station seoulStation;
    private Station yongSanStation;
    private Station hongDaeStation;

    @BeforeEach
    void setUp() {
        lineOne = new Line(LINE_ONE, BACKGROUND_COLOR_BLUE);
        seoulStation = new Station(StationFixtures.SEOUL_STATION);
        yongSanStation = new Station(StationFixtures.YONGSAN_STATION);
        hongDaeStation = new Station(StationFixtures.HONGDAE_STATION);
    }

    @DisplayName("구간의 일급 컬렉션인 Sections에 구간을 추가할 수 있다")
    @Test
    void addSectionWithSizeZero() {
        //given
        Section section = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);

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
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, seoulStation, hongDaeStation, DistanceFixtures.DISTANCE_FIVE);

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
    void addSectionLengthValidate() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_FIVE);
        Section sectionTwo = new Section(lineOne, seoulStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);

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
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);

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
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);

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
        Station songPaStation = new Station(StationFixtures.SONGPA_STATION);
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionThree = new Section(lineOne, hongDaeStation, songPaStation, DistanceFixtures.DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionTwo);
        sections.addSection(sectionThree);
        sections.addSection(sectionOne);

        //then
        List<Station> sortedStations = sections.getSortedStations();
        Assertions.assertThat(sortedStations).containsExactly(seoulStation, yongSanStation, hongDaeStation, songPaStation);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 InvalidValueException이 발생한다")
    @Test
    void addSectionValidateWithAlreadyExists() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);
        Section wrongSection = new Section(lineOne, hongDaeStation, seoulStation, DistanceFixtures.DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);
        sections.addSection(sectionTwo);

        //then
        Assertions.assertThatThrownBy(() -> {
                    sections.addSection(wrongSection);
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.ALREADY_EXISTED_STATIONS_OF_NEW_SECTION.getErrorMessage());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 InvalidValueException이 발생한다")
    @Test
    void addSectionValidateWithNoExists() {
        //given
        Station gangnamStation = new Station(StationFixtures.GANGNAM_STATION);
        Station sinsaStation = new Station(StationFixtures.SHINSA_STATION);
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);
        Section wrongSection = new Section(lineOne, gangnamStation, sinsaStation, DistanceFixtures.DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);
        sections.addSection(sectionTwo);

        //then
        Assertions.assertThatThrownBy(() -> {
                    sections.addSection(wrongSection);
                }).isInstanceOf(InvalidValueException.class)
                .hasMessage(ErrorCode.NOT_EXISTS_STATIONS_OF_NEW_SECTION.getErrorMessage());
    }

    @DisplayName("데이터베이스로 조회된 정렬되지 않는 Section 리스트를 정리해서 VO 객체로 반환한다")
    @Test
    void addSectionAtFirst() {
        //given
        Station songPaStation = new Station(StationFixtures.SONGPA_STATION);
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DistanceFixtures.DISTANCE_TEN);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DistanceFixtures.DISTANCE_TEN);
        Section startSection = new Section(lineOne, songPaStation, seoulStation, DistanceFixtures.DISTANCE_TEN);

        //when
        Sections sections = new Sections();
        sections.addSection(sectionOne);
        sections.addSection(sectionTwo);
        sections.addSection(startSection);

        //then
        Assertions.assertThat(sections.getSections()).containsExactly(startSection, sectionOne, sectionTwo);
    }
}
