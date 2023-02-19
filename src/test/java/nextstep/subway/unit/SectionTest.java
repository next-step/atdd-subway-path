package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SectionTest {

    Station 강남구청역;
    Station 압구정로데오역;
    Line 수인분당선;
    Section 강남구청역_압구정로데오역_구간;

    @BeforeEach
    void setUp() {
        강남구청역 = new Station("강남구청역");
        압구정로데오역 = new Station("압구정로데오역");
        수인분당선 = new Line("수인분당선", "bg-yellow-600");
        강남구청역_압구정로데오역_구간 = new Section(수인분당선, 강남구청역, 압구정로데오역, 10);

    }

    @Test
    void isUpStation() {
        boolean result = 강남구청역_압구정로데오역_구간.isUpStation(강남구청역);

        assertThat(result).isTrue();
    }

    @Test
    void isDownStation() {
        boolean result = 강남구청역_압구정로데오역_구간.isDownStation(압구정로데오역);

        assertThat(result).isTrue();
    }

    @Test
    void updateWhenSectionAddedInMiddle() {
        Station 서울숲역 = new Station("서울숲역");
        int distance = 16;
        Section 강남구청역_서울숲역_구간 = new Section(수인분당선, 강남구청역, 서울숲역, distance);

        강남구청역_서울숲역_구간.updateWhenSectionAddedInMiddle(강남구청역_압구정로데오역_구간);

        assertThat(강남구청역_서울숲역_구간.getUpStation()).isEqualTo(압구정로데오역);
        assertThat(강남구청역_서울숲역_구간.getDownStation()).isEqualTo(서울숲역);
        assertThat(강남구청역_서울숲역_구간.getDistance())
            .isEqualTo(distance - 강남구청역_압구정로데오역_구간.getDistance());
    }
}
