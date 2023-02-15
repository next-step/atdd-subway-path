package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.NotFoundException;
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

        강남역_역삼역_구간 = new Section(line, 강남역, 역삼역, 10);
        역삼역_교대역_구간 = new Section(line, 역삼역, 교대역, 10);

        sections = new Sections();
        sections.add(강남역_역삼역_구간);
        sections.add(역삼역_교대역_구간);
    }

    @DisplayName("포함된 지하철 목록을 중복없이 가져온다.")
    @Test
    void stations() {
        assertThat(sections.sortedStations()).containsExactly(강남역, 역삼역, 교대역);
    }

    @DisplayName("새로운 구간을 하행 종점에 추가할 수 있다.")
    @Test
    void addDownEndSection() {
        // when
        Station 양재역 = new Station(4L, "양재역");
        sections.add(new Section(line, 교대역, 양재역, 10));

        // then
        assertThat(sections.sortedStations()).containsExactly(강남역, 역삼역, 교대역, 양재역);
    }

    @DisplayName("새로운 구간을 기존 구간에 추가할 수 있다.")
    @Test
    void addSection() {
        // when
        Station 양재역 = new Station(4L, "양재역");
        sections.add(new Section(line, 역삼역, 양재역, 5));

        // then
        assertThat(sections.sortedStations()).containsExactly(강남역, 역삼역, 양재역, 교대역);
    }

    @DisplayName("새로운 구간을 상행 종점에 추가할 수 있다.")
    @Test
    void addUpEndSection() {
        // when
        Station 양재역 = new Station(4L, "양재역");
        sections.add(new Section(line, 양재역, 강남역, 10));

        // then
        assertThat(sections.sortedStations()).containsExactly(양재역, 강남역, 역삼역, 교대역);
    }

    @DisplayName("겹치는 구간을 추가하는 경우 예외가 발생한다.")
    @Test
    void addOverlappingSection() {
        // given
        Station 양재역 = new Station(4L, "양재역");
        sections.add(new Section(line, 양재역, 강남역, 10));

        // when
        // then
        assertThatThrownBy(() -> sections.add(new Section(line, 강남역, 교대역, 5)))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("기존에 있는 마지막 구간을 삭제할 수 있다.")
    @Test
    void removeDownEndSection() {
        // when
        sections.remove(교대역.getId());

        // then
        assertThat(sections.sortedStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("기존에 있는 첫번째 구간을 삭제할 수 있다.")
    @Test
    void removeUpEndSection() {
        // when
        sections.remove(강남역.getId());

        // then
        assertThat(sections.sortedStations()).containsExactly(역삼역, 교대역);
    }


    @DisplayName("기존 구간 사이에 있는 역을 삭제할 수 있다.")
    @Test
    void removeBetweenSection() {
        // when
        sections.remove(역삼역.getId());

        // then
        assertThat(sections.sortedStations()).containsExactly(강남역, 교대역);
    }

    @DisplayName("종점역을 제외하고 중간역을 모두 삭제할 수 있다.")
    @Test
    void removeAllBetweenSection() {
        // given
        Station 사당역 = new Station(4L, "사당역");
        sections.add(new Section(line, 교대역, 사당역, 10));

        // when
        sections.remove(역삼역.getId());
        sections.remove(교대역.getId());

        // then
        assertThat(sections.sortedStations()).containsExactly(강남역, 사당역);
    }

    @DisplayName("존재하지 않는 역을 삭제하는 경우 예외가 발생한다.")
    @Test
    void removeUnregisteredStation() {
        assertThatThrownBy(() -> sections.remove(Long.MAX_VALUE))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("구간이 1개인 상태에서 삭제를 하는 경우 예외가 발생한다.")
    @Test
    void removeException() {
        // when
        sections.remove(교대역.getId());

        // then
        assertThatThrownBy(() -> sections.remove(역삼역.getId()))
                .isInstanceOf(SubwayException.class);
    }
}
