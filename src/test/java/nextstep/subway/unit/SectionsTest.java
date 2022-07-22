package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SectionsTest {

    Line 이호선;

    Station 강남역;
    Station 역삼역;

    @BeforeEach
    void setup() {
        이호선 = new Line("2호선", "bg-green-600");

        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
    }

    @Test
    @DisplayName("지하철 구간 등록합니다.")
    void addSection() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Sections 구간 = new Sections();

        구간.addSection(강남_역삼_구간);

        assertThat(구간.getSections()).isEqualTo(List.of(강남_역삼_구간));
    }

    @Test
    @DisplayName("지하철역 조회하기")
    void getStations() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();
        구간.addSection(강남_역삼_구간);

        List<Station> 지하철역들 = 구간.getStations();

        assertThat(지하철역들).isEqualTo(List.of(강남역, 역삼역));
    }

    @Test
    @DisplayName("지하철역 삭제")
    void deleteStation() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();
        구간.addSection(강남_역삼_구간);
        구간.removeStation(역삼역);

        assertThat(구간.getSections()).isEmpty();
    }

    @Test
    @DisplayName("하행선이 아닌 역을 제거할 떄 에러를 반환한다.")
    void deleteStationException() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        Sections 구간 = new Sections();
        구간.addSection(강남_역삼_구간);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            구간.removeStation(강남역);
        });
    }

}
