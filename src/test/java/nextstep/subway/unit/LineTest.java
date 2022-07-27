package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련")
class LineTest {

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 삼성역;

    Line 이호선;

    @BeforeEach
    void setup() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        삼성역 = new Station(4L, "삼성역");

        이호선 = new Line("2호선", "bg-green-600");
    }

    @Test
    @DisplayName("지하철 구간 추가합니다.")
    void addSection() {
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);

        이호선.addSection(강남역_역삼역_구간);

        assertThat(이호선.getSectionList()).containsExactly(강남역_역삼역_구간);
    }

    @Test
    @DisplayName("지하철 구간 정보들을 조회한다.")
    void getStations() {
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 역삼역_선릉역_구간 = new Section(이호선, 역삼역, 선릉역, 4);

        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_선릉역_구간);

        assertThat(이호선.getSectionList()).isEqualTo(List.of(강남역_역삼역_구간, 역삼역_선릉역_구간));
    }

    @Test
    @DisplayName("지하철역 구간이 하나이면 삭제를 시도했을 경우 에러를 반환합니다.")
    void removeStationException() {
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        이호선.addSection(강남역_역삼역_구간);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
            이호선.removeStation(역삼역);
        })
            .withMessage("구간이 하나일때는 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 지하철역을 삭제 시도시 에러를 반환합니다.")
    void isNotExistStation() {
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 역삼역_선릉역_구간 = new Section(이호선, 역삼역, 선릉역, 6);
        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_선릉역_구간);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
                이호선.removeStation(삼성역);
            })
            .withMessage("존재하지 않는 지하철역이라 삭제할 수가 없습니다.");
    }

    @Test
    @DisplayName("지하철 구간 중간에 있는 지하철 역을 삭제합니다.")
    void removeBetweenSection() {
        Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 역삼역_선릉역_구간 = new Section(이호선, 역삼역, 선릉역, 10);
        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_선릉역_구간);

        이호선.removeStation(역삼역);

        assertThat(이호선.getStations()).containsExactly(강남역, 선릉역);
    }


    @Test
    @DisplayName("지하철 노선 업데이트합니다.")
    void updateLine() {
        이호선.update("신분당선", "bg-red-300");

        assertAll(() -> {
            assertThat(이호선.getName()).isEqualTo("신분당선");
            assertThat(이호선.getColor()).isEqualTo("bg-red-300");
        });
    }
}
