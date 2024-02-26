package nextstep.subway.unit;

import nextstep.exception.BadRequestException;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    @Test
    @DisplayName("신규구간이 중간에 추가되어야 하는 역이면 true를 반환한다.")
    void isMiddleSection() {
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");
        Station 잠실역 = new Station(3L, "잠실역");
        Station 선릉_잠실_중간역 = new Station(4L, "역삼역");

        Section 강남_선릉_구간 = new Section(1L, 강남역, 선릉역, 10);
        Section 선릉_잠실_구간 = new Section(2L, 선릉역, 잠실역, 7);
        Sections sections = new Sections(List.of(강남_선릉_구간, 선릉_잠실_구간));
        Section 신규구간 = new Section(3L, 선릉역, 선릉_잠실_중간역, 3);

        boolean middleSectionFlag = sections.isMiddlePositionAddition(신규구간);

        assertThat(middleSectionFlag).isTrue();
    }

    @Test
    @DisplayName("등록할 구간의 상행역과 하행역이 기존 구간의 상행역과 하행역이 동일하면 예외가 발생한다.")
    void validSameSection() {
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");

        Section 기존_구간 = new Section(강남역, 선릉역, 10);
        Section 등록할_구간 = new Section(강남역, 선릉역, 10);

        assertThatThrownBy(() -> 등록할_구간.validMiddleSection(기존_구간))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("등록할 구간이 기존 구간의 길이 보다 길면 예외가 발생한다.")
    void validOverDistanceSection() {
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");

        Section 기존_구간 = new Section(강남역, 선릉역, 10);
        Section 등록할_구간 = new Section(강남역, 역삼역, 13);

        assertThatThrownBy(() -> 기존_구간.validMiddleSection(등록할_구간))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("중간에 추가되어야 하는 역이 잘못된 구간이면 예외가 발생한다.")
    void wrongMiddleSectionException() {
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 잠실역 = new Station("잠실역");
        Station 선릉_잠실_중간역 = new Station("역삼역");

        Section 강남_선릉_구간 = new Section(강남역, 선릉역, 10);
        Section 선릉_잠실_구간 = new Section(선릉역, 잠실역, 7);
        Sections sections = new Sections(List.of(강남_선릉_구간, 선릉_잠실_구간));

        Section 기존과_같은_구간 = new Section(선릉역, 잠실역, 3);
        Section 기존구간_보다_긴_구간 = new Section(선릉역, 선릉_잠실_중간역, 11);

        assertThatThrownBy(() -> sections.isMiddlePositionAddition(기존과_같은_구간))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> sections.isMiddlePositionAddition(기존구간_보다_긴_구간))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("신규구간이 맨앞에 추가되어야 하는 역이면 true를 반환한다.")
    void isFirstSection() {
        Station 강남역 = new Station(1L, "강남역");
        Station 선릉역 = new Station(2L, "선릉역");
        Station 잠실역 = new Station(3L, "잠실역");
        Station 강남역_이전_역 = new Station(4L, "역삼역");

        Section 강남_선릉_구간 = new Section(1L, 강남역, 선릉역, 10);
        Section 선릉_잠실_구간 = new Section(2L, 선릉역, 잠실역, 7);
        Sections sections = new Sections(List.of(강남_선릉_구간, 선릉_잠실_구간));
        Section 신규구간 = new Section(3L, 강남역_이전_역, 강남역, 3);

        boolean firstSectionFlag = sections.isFirstPositionAddition(신규구간);

        assertThat(firstSectionFlag).isTrue();
    }
}
