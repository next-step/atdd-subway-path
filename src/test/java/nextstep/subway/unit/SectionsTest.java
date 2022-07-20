package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SectionsTest {

    @Test
    @DisplayName("구간 등록하기")
    void addSection() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();

        // then
        구간.addSection(강남_역삼_구간);

        assertThat(구간.getSections().get(0)).isEqualTo(강남_역삼_구간);
    }

    @Test
    @DisplayName("역 조회하가")
    void getStations() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();
        구간.addSection(강남_역삼_구간);

        // when
        List<Station> 지하철역들 = 구간.getStations();

        assertThat(지하철역들).hasSize(2);
    }

    @Test
    @DisplayName("지하철역 삭제")
    void deleteStation() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();
        구간.addSection(강남_역삼_구간);
        
        // when
        구간.removeStation(역삼역);

        assertThat(구간.getSections()).isEmpty();
    }

    @Test
    @DisplayName("지하철역 삭제 인데 하행선 아닐때 에러")
    void deleteStationException() {
        Line 이호선 = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();
        구간.addSection(강남_역삼_구간);

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> {
            구간.removeStation(강남역);
        });
    }

}
