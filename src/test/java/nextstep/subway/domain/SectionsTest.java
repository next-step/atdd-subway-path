package nextstep.subway.domain;

import nextstep.subway.exception.section.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Sections의 요구사항 테스트")
class SectionsTest {

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Station 미금역;
    private Section 강남역_정자역;
    private Sections sections;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        미금역 = Station.of("미금역");
        강남역_정자역 = new Section(강남역, 정자역, 10);
        sections = new Sections();

        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(판교역, "id", 2L);
        ReflectionTestUtils.setField(정자역, "id", 3L);
        ReflectionTestUtils.setField(미금역, "id", 4L);
    }

    @DisplayName("첫 구간 등록")
    @Test
    void addSection_first() {
        // when
        sections.addSection(강남역_정자역);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
    }

    @DisplayName("기존 구간의 상행역의 상향 방향에 구간 추가 - 첫 역에 추가")
    @Test
    void addDownSection_first() {
        // given
        sections.addSection(강남역_정자역);
        Section target = new Section(판교역, 강남역, 10);

        // when
        sections.addSection(target);

        // then
        assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(판교역);
        assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(강남역);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("기존 구간의 상행역의 하행 방향에 구간 추가 - 중간 추가")
    @Test
    void addDownSection_middle() {
        // given
        sections.addSection(강남역_정자역);
        Section target = new Section(강남역, 판교역, 4);

        // when
        sections.addSection(target);

        // then
        assertThat(sections.getSections().get(0).getUpStation()).isEqualTo(강남역);
        assertThat(sections.getSections().get(0).getDownStation()).isEqualTo(판교역);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("기존 구간의 하행역의 하행 방향에 구간 추가 - 마지막 역에 추가")
    @Test
    void addDownSection_last() {
        // given
        sections.addSection(강남역_정자역);
        Section target = new Section(정자역, 판교역, 4);

        // when
        sections.addSection(target);

        // then
        assertThat(sections.getSections().get(1).getUpStation()).isEqualTo(정자역);
        assertThat(sections.getSections().get(1).getDownStation()).isEqualTo(판교역);
        assertThat(sections.getSections().size()).isEqualTo(2);
    }

    @DisplayName("추가하려는 구간의 길이는 기존 구간의 길이 보다 길어야 한다")
    @Test
    void addDownSection_fail_distance() {
        // given
        sections.addSection(강남역_정자역);
        Section target = new Section(강남역, 판교역, 20);

        // then
        assertThatThrownBy(() -> sections.addSection(target))
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("이미 추가된 구간을 등록하면 실패")
    @Test
    void addDownSection_fail_alreadyRegisteredSection() {
        // given
        sections.addSection(강남역_정자역);
        Section target = new Section(강남역, 정자역, 5);

        // then
        assertThatThrownBy(() -> sections.addSection(target))
                .isInstanceOf(AlreadyRegisteredStationException.class);
    }

    @DisplayName("추가하려는 간의 역은 기존 구간들의 역에 하나라도 포함돼야 한다")
    @Test
    void addDownSection_fail_ConnectStation() {
        // given
        sections.addSection(강남역_정자역);
        Section target = new Section(판교역, 미금역, 5);

        // then
        assertThatThrownBy(() -> sections.addSection(target))
                .isInstanceOf(NotFoundConnectStationException.class);
    }

    @DisplayName("마지막 역을 삭제할 수 있다")
    @Test
    void deleteStation() {
        // given
        sections.addSection(강남역_정자역);
        Section section = new Section(강남역, 판교역, 5);
        sections.addSection(section);

        // when
        sections.deleteStation(정자역);

        // then
        assertThat(sections.getSections().size()).isEqualTo(1);
        assertThat(sections.getAllStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("역을 삭제할 경우 구간이 최소 1개는 있어야 한다")
    @Test
    void deleteStation_validateMinimumSection() {
        // given
        sections.addSection(강남역_정자역);

        // then
        assertThatThrownBy(() -> sections.deleteStation(정자역))
                .isInstanceOf(MinimumSectionException.class);
    }

    @DisplayName("마지막 하행역만 삭제 가능하다")
    @Test
    void deleteStation_validateDeleteLastDownStation() {
        // given
        sections.addSection(강남역_정자역);

        // then
        assertThatThrownBy(() -> sections.deleteStation(강남역))
                .isInstanceOf(DeleteLastDownStationException.class);
    }

}