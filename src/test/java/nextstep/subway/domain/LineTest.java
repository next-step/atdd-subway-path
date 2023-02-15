package nextstep.subway.domain;

import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    public void setUp() {
        이호선 = new Line("2호선", "bg-green-600");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
    }

    @DisplayName("노선 정보 수정")
    @Test
    void update() {
        // when
        이호선.update("신분당선", "bg-red-600");

        // then
        assertThat(이호선.getName()).isEqualTo("신분당선");
        assertThat(이호선.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("두 역 사이에 구간 추가")
    @Test
    void addSectionBetweenStations() {
        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(강남역, 선릉역, 9);

        // then
        assertThat(이호선.getStations()).containsExactly(강남역, 선릉역, 역삼역);
        assertThat(이호선.getSections().stream()
                .filter(section -> section.getUpStation().equals(강남역))
                .filter(section -> section.getDownStation().equals(선릉역))
                .findFirst().get()
                .getDistance()).isEqualTo(9);
        assertThat(이호선.getSections().stream()
                .filter(section -> section.getUpStation().equals(선릉역))
                .filter(section -> section.getDownStation().equals(역삼역))
                .findFirst().get()
                .getDistance()).isEqualTo(1);
    }

    @DisplayName("상행종점역에 대한 구간 추가")
    @Test
    void addSectionWithLastUpStation() {
        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(선릉역, 강남역, 10);

        // then
        assertThat(이호선.getStations()).containsExactly(선릉역, 강남역, 역삼역);
    }

    @DisplayName("하행종점역에 대한 구간 추가")
    @Test
    void addSectionWithLastDownStation() {
        // when
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // then
        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("두 역 사이에 구간을 추가할 때, 기존 구간보다 길이가 같거나 길 경우, 오류 발생")
    @Test
    void cannotAddSectionBetweenStationsWithInvalidDistance() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(강남역, 선릉역, 10);
        }).isInstanceOf(BusinessException.class);

        assertThatThrownBy(() -> {
            이호선.addSection(강남역, 선릉역, 11);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("추가하려는 구간의 상행역과 하행역 모두 노선에 존재할 경우, 오류 발생")
    @Test
    void cannotAddSectionUpStationAndDownStationIsAlreadyExists() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(강남역, 선릉역, 3);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("추가하려는 구간의 상행역과 하행역 모두 노선에 존재하지 않을 경우, 오류 발생")
    @Test
    void cannotAddSectionUpStationAndDownStationDoesNotContainInLine() {
        // given
        final Station 당산역 = new Station("당산역");
        final Station 선유도역 = new Station("선유도역");
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.addSection(당산역, 선유도역, 7);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("노선의 역 목록 조회")
    @Test
    void getStations() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when
        final List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역);
    }

    @DisplayName("마지막 구간 제거")
    @Test
    void removeLastSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when
        이호선.removeSection(선릉역);

        // then
        final List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(강남역, 역삼역);
    }

    @DisplayName("중간 구간 제거")
    @Test
    void removeMiddleSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);

        // when
        이호선.removeSection(역삼역);

        // then
        final List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(강남역, 선릉역);

        assertThat(이호선.getSections().stream()
                .filter(section -> section.getUpStation().equals(강남역))
                .filter(section -> section.getDownStation().equals(선릉역))
                .findFirst().get()
                .getDistance()).isEqualTo(20);
    }

    @DisplayName("마지막 구간을 제거하려고 하는 경우, 오류 발생")
    @Test
    void cannotRemoveLastRemainingSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.removeSection(역삼역);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("노선에 존재하지 않는 역을 제거하려고 할 경우, 오류 발생")
    @Test
    void cannotRemoveSectionWithNotContainsStation() {
        // when, then
        assertThatThrownBy(() -> {
            이호선.removeSection(역삼역);
        }).isInstanceOf(BusinessException.class);
    }

    @Test
    void cannotRemoveLastRemainingSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            이호선.removeSection(역삼역);
        }).isInstanceOf(BusinessException.class);
    }

    @Test
    void cannotRemoveSectionWithNotContainsStation() {
        // when, then
        assertThatThrownBy(() -> {
            이호선.removeSection(역삼역);
        }).isInstanceOf(BusinessException.class);
    }
}
