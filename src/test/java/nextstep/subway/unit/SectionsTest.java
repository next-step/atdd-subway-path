package nextstep.subway.unit;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.fixture.SectionFixture.*;
import static nextstep.subway.fixture.StationFixture.*;

class SectionsTest {

    @Test
    @DisplayName("모든역을 가져오는지 확인")
    void getSectionsTest() {
        // given
        Sections sections = new Sections();

        sections.addSection(강남_양재_구간);
        sections.addSection(양재_청계산_구간);
        sections.addSection(청계산_정자_구간);

        // when
        List<Station> stations = sections.getStations();

        // then
        Assertions.assertThat(stations)
                .map(Station::getName)
                .containsExactly(강남역.getName(), 양재역.getName(), 청계산역.getName(), 정자역.getName());
    }
}