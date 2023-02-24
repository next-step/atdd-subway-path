package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    private Station 강남역;
    private Station 정자역;
    private Station 광교역;

    private Line 이호선;

    private Sections sections;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
        광교역 = new Station("광교역");
        이호선 = new Line("2호선", "green", 강남역, 정자역, 10);
        sections = new Sections();
    }

    @DisplayName("구간을 추가")
    @Test
    void addSection() {
        sections.addSection(이호선, 강남역, 정자역, 10);
        assertThat(sections.getUpStation()).isEqualTo(강남역);
        assertThat(sections.getDownStation()).isEqualTo(정자역);
    }

    @DisplayName("구간을 두 개 추가하고, 역 리스트를 가져오는 기능을 검증")
    @Test
    void getStations() {
        sections.addSection(이호선, 강남역, 정자역, 10);
        sections.addSection(이호선, 정자역, 광교역, 10);
        assertThat(sections.getStations()).containsExactly(강남역, 정자역, 광교역);
    }

    @DisplayName("상행 종점역 검증")
    @Test
    void getUpStation() {
        sections.addSection(이호선, 강남역, 정자역, 10);
        Section section = sections.getSections().get(0);
        assertThat(sections.getUpStation()).isEqualTo(section.getUpStation());
    }

    @DisplayName("하행 종점역 검증")
    @Test
    void getDownStation() {
        sections.addSection(이호선, 강남역, 정자역, 10);
        Section section = sections.getSections().get(0);
        assertThat(sections.getDownStation()).isEqualTo(section.getDownStation());
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        sections.addSection(이호선, 강남역, 정자역, 10);
        sections.addSection(이호선, 정자역, 광교역, 10);
        sections.removeSection(이호선,정자역);
        assertThat(sections.getStations()).containsExactly(강남역, 광교역);
        assertThat(sections.getFirstSection().getDistance()).isEqualTo(20);
    }
}
