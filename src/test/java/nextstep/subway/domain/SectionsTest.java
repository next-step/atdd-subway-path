package nextstep.subway.domain;

import nextstep.subway.exception.section.AlreadyRegisteredStationException;
import nextstep.subway.exception.section.InvalidDistanceException;
import nextstep.subway.exception.section.MinimumSectionException;
import nextstep.subway.exception.section.NotFoundConnectStationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Sections의 요구사항 테스트")
class SectionsTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Station 미금역;
    private Line 신분당선;
    private Section 강남역_정자역;
    private Sections sections;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        미금역 = Station.of("미금역");
        신분당선 = new Line();
        강남역_정자역 = Section.of(신분당선, 강남역, 정자역, 10);
        sections = new Sections();
        sections.addSection(강남역_정자역);
    }

    @DisplayName("첫 구간 등록")
    @Test
    void addSection_first() {
        // given
        Sections sections = new Sections();

        // when
        sections.addSection(강남역_정자역);

        // then
        assertThat(sections.getAllStations()).containsExactly(강남역, 정자역);
    }

    @DisplayName("기존 구간의 상행역의 상향 방향에 구간 추가 - 첫 역에 추가")
    @Test
    void addDownSection_first() {
        // given
        Section section = Section.of(신분당선, 판교역, 강남역, 10);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getAllStations()).containsExactly(판교역, 강남역, 정자역);
    }

    @DisplayName("기존 구간의 상행역의 하행 방향에 구간 추가 - 중간 추가")
    @Test
    void addDownSection_middle() {
        // given
        Section section = Section.of(신분당선, 강남역, 판교역, 4);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getAllStations()).containsExactly(강남역, 판교역, 정자역);
    }

    @DisplayName("기존 구간의 하행역의 하행 방향에 구간 추가 - 마지막 역에 추가")
    @Test
    void addDownSection_last() {
        // given
        Section section = Section.of(신분당선, 정자역, 미금역, 4);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getAllStations()).containsExactly(강남역, 정자역, 미금역);
    }

    @DisplayName("추가하려는 구간의 길이는 기존 구간의 길이 보다 길어야 한다")
    @Test
    void addDownSection_fail_distance() {
        // given
        Section section = Section.of(신분당선, 강남역, 판교역, 20);

        // then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("이미 추가된 구간을 등록하면 실패")
    @Test
    void addDownSection_fail_alreadyRegisteredSection() {
        // given
        Section section = Section.of(신분당선, 강남역, 정자역, 5);

        // then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(AlreadyRegisteredStationException.class);
    }

    @DisplayName("추가하려는 간의 역은 기존 구간들의 역에 하나라도 포함돼야 한다")
    @Test
    void addDownSection_fail_ConnectStation() {
        // given
        Section section = Section.of(신분당선, 판교역, 미금역, 5);

        // then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(NotFoundConnectStationException.class);
    }

    @DisplayName("첫 번째 역을 삭제할 수 있다")
    @Test
    void deleteStation_firstStation() {
        // given
        Section section = Section.of(신분당선, 강남역, 판교역, 5);
        sections.addSection(section);

        // when
        sections.deleteStation(강남역);

        // then
        assertThat(sections.getAllStations()).containsExactly(판교역, 정자역);
    }

    @DisplayName("마지막 역을 삭제할 수 있다")
    @Test
    void deleteStation_lastStation() {
        // given
        Section section = Section.of(신분당선, 강남역, 판교역, 5);
        sections.addSection(section);

        // when
        sections.deleteStation(정자역);

        // then
        assertThat(sections.getAllStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("역을 삭제할 경우 구간이 최소 1개는 있어야 한다")
    @Test
    void deleteStation_validateMinimumSection() {
        // then
        assertThatThrownBy(() -> sections.deleteStation(정자역))
                .isInstanceOf(MinimumSectionException.class);
    }

}