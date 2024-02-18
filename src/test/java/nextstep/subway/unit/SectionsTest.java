package nextstep.subway.unit;

import nextstep.exception.BadRequestException;
import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    @Test
    @DisplayName("신규구간의 하행역과 기존구간의 하행역으로부터 새로운 구간을 생성한다")
    void createSectionByRequestSectionAndExistingSection() {
        Station 기존구간_상행역 = new Station("강남역");
        Station 기존구간_하행역 = new Station("선릉역");
        Station 신규구간_하행역 = new Station("역삼역");

        Section 기존구간 = new Section(기존구간_상행역, 기존구간_하행역, 10);
        Sections sections = new Sections(List.of(기존구간));
        Section 신규구간 = new Section(기존구간_상행역, 신규구간_하행역, 3);

        //when
        Section newSection = sections.createNewSection(기존구간, 신규구간);

        //then
        assertAll(
                () -> assertThat(newSection.getUpStation()).isEqualTo(신규구간_하행역),
                () -> assertThat(newSection.getDownStation()).isEqualTo(기존구간_하행역),
                () -> assertThat(newSection.getDistance()).isEqualTo(기존구간.getDistance()-신규구간.getDistance())
        );
    }

    @Test
    @DisplayName("두 개의 구간의 상행역과 하행역이 동일하면 예외가 발생한다.")
    void validSameSection() {
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");

        Section 기존_구간 = new Section(강남역, 선릉역, 10, null);
        Section 등록할_구간 = new Section(강남역, 선릉역, 10, null);

        assertThatThrownBy(() -> 등록할_구간.validMiddleSection(기존_구간))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("등록할 구간이 기존 구간의 길이 보다 길면 예외가 발생한다.")
    void validOverDistanceSection() {
        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 역삼역 = new Station("역삼역");

        Section 기존_구간 = new Section(강남역, 선릉역, 10, null);
        Section 등록할_구간 = new Section(강남역, 역삼역, 13, null);

        assertThatThrownBy(() -> 등록할_구간.validMiddleSection(기존_구간))
                .isInstanceOf(BadRequestException.class);
    }

}
