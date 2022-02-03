package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.exception.AddSectionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LineTest {

    Station 가양역;
    Station 증미역;
    Station 등촌역;
    Station 신목동역;
    Line line;
    int tenDistance;
    int fourDistance2;

    @BeforeEach
    void setUp() {
        가양역 = new Station("가양역");
        증미역 = new Station("증미역");
        등촌역 = new Station("등촌역");
        신목동역 = new Station("신목동역");
        line = new Line("9호선", "금색");
        tenDistance = 10;
        fourDistance2 = 4;
    }

    @DisplayName("지하철역 사이에 새로운 구간 추가")
    @Test
    void addLineBetweenSection() {
        // given
        Section section1 = new Section(line, 가양역, 등촌역, tenDistance);
        Section section2 = new Section(line, 가양역, 증미역, fourDistance2);

        // when
        line.addSection(section1);
        line.addSection(section2);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections.size()).isEqualTo(2);
        assertThat(section1.getUpStation()).isEqualTo(증미역);
        assertThat(section1.getDownStation()).isEqualTo(등촌역);
        assertThat(section1.getDistance()).isEqualTo(tenDistance - fourDistance2);
    }

    @DisplayName("지하철 노선의 하행 종점역에 구간을 추가")
    @Test
    void addLineDownEndStationSection() {
        // given
        Section section = new Section(line, 가양역, 증미역, tenDistance);

        // when
        line.addSection(section);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections.size()).isEqualTo(1);
        assertThat(section.getUpStation()).isEqualTo(가양역);
        assertThat(section.getDownStation()).isEqualTo(증미역);
        assertThat(section.getDistance()).isEqualTo(tenDistance);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        line.addSection(new Section(line, 등촌역, 신목동역, tenDistance));
        line.addSection(new Section(line, 증미역, 등촌역, fourDistance2));
        line.addSection(new Section(line, 가양역, 증미역, fourDistance2));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(가양역, 증미역, 등촌역, 신목동역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        line.addSection(new Section(line, 가양역, 증미역, tenDistance));
        line.addSection(new Section(line, 증미역, 등촌역, fourDistance2));

        // when
        line.removeSection(등촌역);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0).getDownStation()).isEqualTo(증미역);
    }

    @DisplayName("지하철역 사이에 새로운 구간 추가 시 기존 역 사이 길이 이상일 수 없음.")
    @Test
    void exceptionAddLineBetweenSection() {
        // given
        Section section1 = new Section(line, 가양역, 등촌역, tenDistance);
        Section section2 = new Section(line, 가양역, 증미역, tenDistance);
        line.addSection(section1);


        // when
        assertThatThrownBy(() -> line.addSection(section2))
                // then
                .isInstanceOf(AddSectionException.class)
                .hasMessage("새로 추가되는 구간 거리는 기존 구간의 거리 이상일 수 없습니다. 기존 구간 거리 = 10, 신규 구간 거리 = 10");
    }

    @DisplayName("구간 추가 시 상행역과 하행역이 모두 등록된 역일 수 없음.")
    @Test
    void exceptionAddSectionDuplicate() {
        // given
        Section section1 = new Section(line, 가양역, 증미역, tenDistance);
        Section section2 = new Section(line, 가양역, 증미역, tenDistance);
        line.addSection(section1);

        // when
        assertThatThrownBy(() -> line.addSection(section2))
                // then
                .isInstanceOf(AddSectionException.class)
                .hasMessage("상행역과 하행역 모두 등록된 역입니다. 상행역 = 가양역, 하행역 = 증미역");
    }

    @DisplayName("구간 추가 시 상행역과 하행역 중 하나도 구간에 등록되어 있지 않으면 등록 불가.(구간이 1개 이상일 경우)")
    @Test
    void exceptionAddSectionNotFoundStation() {
        // given
        Section section1 = new Section(line, 가양역, 증미역, tenDistance);
        Section section2 = new Section(line, 등촌역, 신목동역, tenDistance);
        line.addSection(section1);

        // when
        assertThatThrownBy(() -> line.addSection(section2))
                // then
                .isInstanceOf(AddSectionException.class)
                .hasMessage("상행역과 하행역 모두 구간에 존재하지 않는 역입니다. 상행역 = 등촌역, 하행역 = 신목동역");
    }
}
