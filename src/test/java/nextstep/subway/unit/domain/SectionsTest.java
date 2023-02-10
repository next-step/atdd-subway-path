package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.SubwayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[Domain] 지하철 구간 일급 컬렉션 테스트")
class SectionsTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private Station 양재역;
    private Line line;
    private Section 강남역_역삼역_구간;
    private Section 역삼역_교대역_구간;
    private Sections sections;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        교대역 = new Station(3L, "교대역");
        양재역 = new Station(4L, "양재역");

        강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 10);
        역삼역_교대역_구간 = new Section(line, 역삼역, 교대역, 10);

        sections = new Sections();
        sections.add(강남역_역삼역_구간);
        sections.add(역삼역_교대역_구간);
    }

    @DisplayName("포함된 지하철 목록을 중복없이 가져온다.")
    @Test
    void stations() {
        assertThat(sections.stations()).containsExactly(강남역, 역삼역, 교대역);
    }

    @DisplayName("새로운 구간을 추가할 수 있다.")
    @Test
    void add() {
        sections.add(new Section(line, 교대역, 양재역, 10));

        assertThat(sections.stations()).containsExactly(강남역, 역삼역, 교대역, 양재역);
    }

    @DisplayName("기존에 있는 마지막 구간을 삭제할 수 있다.")
    @Test
    void remove() {
        sections.remove(교대역.getId());

        assertThat(sections.stations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("마지막 구간이 아닌 역을 삭제하는 경우 예외가 발생한다.")
    @Test
    void removeException1() {
        assertThatThrownBy(() -> sections.remove(강남역.getId()))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간이 1개인 상태에서 삭제를 하는 경우 예외가 발생한다.")
    @Test
    void removeException3() {
        sections.remove(교대역.getId());

        assertThatThrownBy(() -> sections.remove(역삼역.getId()))
                .isInstanceOf(SubwayException.class);
    }
}
