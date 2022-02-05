package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    Station 강남역;
    Station 판교역;
    Station 정자역;
    Sections 신분당선_구간들;

    @BeforeEach
    public void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        정자역 = new Station("정자역");
        신분당선_구간들 = new Sections(new Section(강남역, 판교역, 8));
        신분당선_구간들.addSection(new Section(판교역, 정자역, 3));
    }

    @Test
    void getFirstUpStation() {
        // when
        Station firstUpStation = 신분당선_구간들.getFirstUpStation();

        // then
        assertThat(firstUpStation).isEqualTo(강남역);
    }

    @Test
    void getLastDownStation() {
        // when
        Station lastDownStation = 신분당선_구간들.getLastDownStation();

        // then
        assertThat(lastDownStation).isEqualTo(정자역);
    }
}
