package nextstep.subway.unit;

import nextstep.subway.section.Section;
import nextstep.subway.section.Sections;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

}
