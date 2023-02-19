package nextstep.subway.domain;

import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();
        이호선 = new Line(1L, "2호선", "bg-green-color");
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(1L, "역삼역");
        선릉역 = new Station(1L, "선릉역");
    }

    @DisplayName("두 역 사이에 구간 추가")
    @Test
    void addSectionBetweenStations() {
        // when
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 강남역, 선릉역, 9);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 선릉역, 역삼역);
        assertThat(sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(강남역))
                .filter(section -> section.getDownStation().equals(선릉역))
                .findFirst().get()
                .getDistance()).isEqualTo(9);
        assertThat(sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(선릉역))
                .filter(section -> section.getDownStation().equals(역삼역))
                .findFirst().get()
                .getDistance()).isEqualTo(1);
    }

    @DisplayName("첫 구간으로 구간 추가")
    @Test
    void addSectionWithLastUpStation() {
        // when
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 선릉역, 강남역, 10);

        // then
        assertThat(sections.getStations()).containsExactly(선릉역, 강남역, 역삼역);
    }

    @DisplayName("마지막 구간으로 구간 추가")
    @Test
    void addSectionWithLastDownStation() {
        // when
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // then
        assertThat(sections.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("역 사이에 구간을 추가할 때, 기존 구간의 길이보다 같거나 긴 구간을 추가할 경우 에러 발생")
    @Test
    void cannotAddSectionBetweenStationsWithInvalidDistance() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.add(이호선, 강남역, 선릉역, 10);
        }).isInstanceOf(BusinessException.class);

        assertThatThrownBy(() -> {
            sections.add(이호선, 강남역, 선릉역, 11);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("추가하려는 구간의 상행역과 하행역 모두 지하철 노선에 이미 등록되어있을 경우 에러 발생")
    @Test
    void cannotAddSectionUpStationAndDownStationIsAlreadyExists() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.add(이호선, 강남역, 선릉역, 3);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("추가하려는 구간의 상행역과 하행역 모두 지하철 노선에 등록되어있지 않은 경우 에러 발생")
    @Test
    void cannotAddSectionUpStationAndDownStationDoesNotContainInLine() {
        // given
        final Station 당산역 = new Station("당산역");
        final Station 선유도역 = new Station("선유도역");
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.add(이호선, 당산역, 선유도역, 7);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("지하철 노선의 역 목록 조회")
    @Test
    void getStations() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);

        // when
        final List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsAnyOf(강남역, 역삼역);
    }

    @DisplayName("마지막 구간 제거")
    @Test
    void removeLastSection() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);
        sections.add(이호선, 역삼역, 선릉역, 10);

        // when
        sections.remove(선릉역);

        // then
        final List<Station> stations = sections.getStations();
        assertThat(stations).containsOnly(강남역, 역삼역);
    }

    @DisplayName("마지막으로 남은 구간을 제거하려고 할 경우 에러 발생")
    @Test
    void cannotRemoveLastRemainingSection() {
        // given
        sections.add(이호선, 강남역, 역삼역, 10);

        // when, then
        assertThatThrownBy(() -> {
            sections.remove(역삼역);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("제거하려는 구간의 역이 지하철 노선에 포함되어있지 않은 경우 에러 발생")
    @Test
    void cannotRemoveSectionWithNotContainsStation() {
        // when, then
        assertThatThrownBy(() -> {
            sections.remove(역삼역);
        }).isInstanceOf(BusinessException.class);
    }
}
