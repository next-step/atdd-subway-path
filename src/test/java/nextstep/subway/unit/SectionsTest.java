package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.error.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("하행 종점역으로 구간 등록")
    @Test
    void addSectionWithDownStation() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(역삼역_삼성역_구간);

        // then
        assertThat(이호선.getStations().getList()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @DisplayName("역과 역 사이에 구간 등록")
    @Test
    void addSectionBetweenStations() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 강남역_삼성역_구간 = new Section(이호선, 강남역, 삼성역, 8);
        이호선.addSection(강남역_삼성역_구간);

        // then
        assertThat(이호선.getStations().getList()).containsExactly(강남역, 삼성역, 역삼역);
        assertThat(이호선.getTotalDistance()).isEqualTo(10);
    }

    @DisplayName("상행 종점역으로 구간 등록")
    @Test
    void addSectionWithUpStation() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 역삼역_삼성역_구간 = new Section(이호선, 삼성역, 강남역, 10);
        이호선.addSection(역삼역_삼성역_구간);

        // then
        assertThat(이호선.getStations().getList()).containsExactly(삼성역, 강남역, 역삼역);
    }

    @DisplayName("[Error] 역 사이에 구간을 추가할 때 기존 역 사이 길이보다 크거나 같은 구간을 추가")
    @Test
    void addSectionWithInvalidDistance() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 강남역_삼성역_구간 = new Section(이호선, 강남역, 삼성역, 10);
        assertThatThrownBy(() -> {
            이호선.addSection(강남역_삼성역_구간);
        }).isInstanceOf(BusinessException.class);

        final Section 다른_강남역_삼성역_구간 = new Section(이호선, 강남역, 삼성역, 15);
        assertThatThrownBy(() -> {
            이호선.addSection(다른_강남역_삼성역_구간);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("[Error] 상행역, 하행역이 모두 등록되어 있는 구간 추가")
    @Test
    void addSectionWithExistsUpStationAndDownStation() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 같은_강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 8);
        assertThatThrownBy(() -> {
            이호선.addSection(같은_강남역_역삼역_구간);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("[Error] 상행역, 하행역이 모두 등록되어있지 않은 구간 추가")
    @Test
    void addSectionWithNonExistsUpStationAndDownStation() {
        // given
        final Station 잠실역 = new Station("잠실역");
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 삼성역_잠실역_구간 = new Section(이호선, 삼성역, 잠실역, 8);
        assertThatThrownBy(() -> {
            이호선.addSection(삼성역_잠실역_구간);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("하행 종점역 구간 제거")
    @Test
    void removeSectionWithDownStation() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_삼성역_구간);

        // When
        이호선.removeSection(삼성역);

        // Then
        assertThat(이호선.getStations().getList()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("상행 종점역 구간 제거")
    @Test
    void removeSectionWithUpStation() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_삼성역_구간);

        // When
        이호선.removeSection(강남역);

        // Then
        assertThat(이호선.getStations().getList()).containsExactly(역삼역, 삼성역);
    }

    @DisplayName("역과 역 사이의 구간 제거")
    @Test
    void removeSectionBetweenStations() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_삼성역_구간);

        // When
        이호선.removeSection(역삼역);

        // Then
        assertThat(이호선.getStations().getList()).containsExactly(강남역, 삼성역);
    }

    @DisplayName("[Error] 노선에 존재하지 않는 역에 한 구간을 제거")
    @Test
    void removeSectionAboutNonExistsStationInLine() {
        // Given
        final Station 잠실역 = new Station("잠실역");
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(강남역_역삼역_구간);
        이호선.addSection(역삼역_삼성역_구간);

        // When
        assertThatThrownBy(() -> {
            이호선.removeSection(잠실역);
        }).isInstanceOf(BusinessException.class);
    }

    @DisplayName("[Error] 마지막 남은 구간을 삭제")
    @Test
    void removeSectionWithLastSection() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        assertThatThrownBy(() -> {
            이호선.removeSection(역삼역);
        }).isInstanceOf(BusinessException.class);
    }
}
