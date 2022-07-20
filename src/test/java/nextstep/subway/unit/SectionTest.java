package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    @Test
    @DisplayName("구간을 생성한다.")
    void createSection() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        assertAll(() -> {
            assertThat(강남_역삼_구간.getLine()).isEqualTo(이호선);
            assertThat(강남_역삼_구간.getUpStation()).isEqualTo(강남역);
            assertThat(강남_역삼_구간.getDownStation()).isEqualTo(역삼역);
        });
    }

    @Test
    @DisplayName("지하철역을 조회 한다.")
    void getStations() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        List<Station> 지하철역 = 강남_역삼_구간.getStations();

        assertAll(() -> {
            assertThat(지하철역.get(0)).isEqualTo(강남역);
            assertThat(지하철역.get(1)).isEqualTo(역삼역);
        });
    }

}
