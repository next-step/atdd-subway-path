package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line line;
    Station 강남역;
    Station 역삼역;
    Station 홍대역;

    @BeforeEach
    void setup() {
        line = 노선_생성();
        역목록_초기화();
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        기존_구간_추가();

        // when
        Section 새로운_구간 = 새로운_구간_추가();

        // then
        assertThat(line.getSections().get(1)).isEqualTo(새로운_구간);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Section 기존_구간 = 기존_구간_추가();
        Section 새로운_구간 = 새로운_구간_추가();

        // when
        List<Section> sections = line.getSections();

        // then
        assertThat(sections).isEqualTo(List.of(기존_구간, 새로운_구간));
    }

    @DisplayName("구간 목록에서 마지막 역 삭제 = 마지막 구간 제거")
    @Test
    void removeSection() {
        // given
        Section 기존_구간 = 기존_구간_추가();
        새로운_구간_추가();

        // when
        line.deleteSection(3L);

        // then
        List<Section> 삭제후_구간_목록 = line.getSections();
        assertThat(마지막_구간_of(삭제후_구간_목록)).isEqualTo(기존_구간);
    }

    private Line 노선_생성() {
        return Line.builder()
                .name("2호선")
                .color("green")
                .build();
    }

    private void 역목록_초기화() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        홍대역 = new Station(3L, "홍대역");
    }

    private Section 기존_구간_추가() {
        Section section = Section.builder()
                .id(1L)
                .line(line)
                .upStation(강남역)
                .downStation(역삼역)
                .distance(5)
                .build();
        line.addSection(section);
        return section;
    }

    private Section 새로운_구간_추가() {
        Section section = Section.builder()
                .id(2L)
                .line(line)
                .upStation(역삼역)
                .downStation(홍대역)
                .distance(3)
                .build();
        line.addSection(section);
        return section;

    }

    private Section 마지막_구간_of(List<Section> sections) {
        return sections.get(sections.size() - 1);
    }
}
