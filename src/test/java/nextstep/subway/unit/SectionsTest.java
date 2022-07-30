package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private final Line line = new Line("2호선", "green");
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();
    }

    @Test
    @DisplayName("첫 구간 등록")
    void addSections() {
        //when
        구간_등록(강남역(), 역삼역(), 10);

        //then
        assertThat(sections.sections()).hasSize(1);
    }

    private void 구간_등록(final Station upStation, final Station downStation, final int distance) {
        sections.addSections(line, upStation, downStation, distance);
    }

    @Test
    @DisplayName("구간이 있는경우 전 구간 조회")
    void getStationsNotEmpty() {
        구간_등록(강남역(), 역삼역(), 10);

        assertThat(sections.getStations())
            .containsExactly(강남역(), 역삼역());
    }

    @Test
    @DisplayName("구간이 없는경우 빈 리스트 반환")
    void getStationsIsEmpty() {
        assertThat(sections.getStations()).isEmpty();
    }

    @Test
    @DisplayName("구간이 없는경우 삭제 시 예외")
    void removeStationsIsEmpty() {
        assertThatThrownBy(() -> sections.removeStations(강남역()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("등록된 구간이 없습니다.");
    }

    @Test
    @DisplayName("구간 하행 종점역 정보가 맞지 않는 경우 예외")
    void removeStationsNotEqualDownStation() {
        구간_등록(강남역(), 역삼역(), 10);
        assertThatThrownBy(() -> sections.removeStations(잠실역()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("하행 종점역 정보가 다릅니다.");
    }

    @Test
    @DisplayName("사이 역 추가 - 앞구간을 기준으로 추가하는 경우")
    void addMiddleSectionsFront() {
        구간_등록(강남역(), 역삼역(), 10);
        sections.addSections(line, 강남역(), 잠실역(), 7);

        assertThat(sections.sections()).containsExactly(
            new Section(line, 강남역(), 잠실역(), 7),
            new Section(line, 잠실역(), 역삼역(), 3)
        );
    }

    @Test
    @DisplayName("사이 역 추가 - 뒷 구간을 기준으로 추가하는 경우")
    void addMiddleSectionsBack() {
        구간_등록(강남역(), 역삼역(), 10);
        sections.addSections(line, 잠실역(), 역삼역(), 7);

        assertThat(sections.sections()).containsExactly(
            new Section(line, 강남역(), 잠실역(), 3),
            new Section(line, 잠실역(), 역삼역(), 7)
        );
    }

    @Test
    @DisplayName("이미 등록이 되어있는 구간의 경우 예외")
    void alreadyRegisteredSection() {
        구간_등록(강남역(), 역삼역(), 10);
        assertThatIllegalStateException().isThrownBy(() -> 구간_등록(강남역(), 역삼역(), 10))
            .withMessage("이미 등록되어있는 구간입니다.");
    }

    @Test
    @DisplayName("상행, 하행역 전부 포함되어있지 않을 시 예외")
    void isNotMatchStationAnyWhere() {
        //when
        구간_등록(강남역(), 역삼역(), 10);

        //then
        assertThatIllegalStateException().isThrownBy(() -> sections.addSections(line, 잠실역(), new Station(4L, "서초역"), 10))
            .withMessage("상행, 하행역이 모두 포함되어있지 않습니다.");
    }

    @Test
    @DisplayName("맨 앞 구간 조회")
    void getFirstSection() {
        //given
        구간_등록(강남역(), 역삼역(), 10);

        //when
        sections.addSections(line, 잠실역(), 강남역(), 4);

        //then
        assertThat(sections.isFirstSection(new Section(line, 잠실역(), 강남역(), 4))).isTrue();
    }

    @Test
    @DisplayName("구간 정렬")
    void sectionSort() {
        //given
        구간_등록(강남역(), 역삼역(), 10);
        구간_등록(강남역(), 잠실역(), 7);

        //when
        final List<Section> sortedSections = sections.sections();

        //then
        assertThat(sortedSections).containsExactly(
            new Section(line, 강남역(), 잠실역(), 7),
            new Section(line, 잠실역(), 역삼역(), 3)
        );
    }

    @Test
    @DisplayName("역의 id 가 구간에 포함되어있는지 검증")
    void containsStation() {
        //given
        구간_등록(강남역(), 역삼역(), 10);

        //when
        final boolean actual = sections.containsStation(2L);

        //then
        assertThat(actual).isTrue();
    }

    private Station 강남역() {
        return new Station(1L, "강남역");
    }

    private Station 역삼역() {
        return new Station(2L, "역삼역");
    }

    private Station 잠실역() {
        return new Station(3L, "잠실역");
    }

}