package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    Station 가양역;
    Station 증미역;
    Station 등촌역;
    Station 신목동역;
    Line line;
    int tenDistance;
    int fourDistance;

    @BeforeEach
    void setUp() {
        가양역 = new Station("가양역");
        증미역 = new Station("증미역");
        등촌역 = new Station("등촌역");
        신목동역 = new Station("신목동역");
        line = new Line("9호선", "금색");
        tenDistance = 10;
        fourDistance = 4;
    }

    @DisplayName("구간 사이에 역 추가")
    @Test
    void updateAddLineBetweenSection() {
        // given
        Section section1 = new Section(line, 가양역, 등촌역, tenDistance);
        Section newSection = new Section(line, 가양역, 증미역, fourDistance);

        // when
        section1.updateAddLineBetweenSection(newSection);

        // then
        assertThat(section1.getUpStation()).isEqualTo(증미역);
        assertThat(section1.getDownStation()).isEqualTo(등촌역);
        assertThat(section1.getDistance()).isEqualTo(6);
        assertThat(newSection.getDistance()).isEqualTo(4);
    }

    @DisplayName("구간 사이의 역 삭제 시 다음 구간의 상행역에 삭제된 하행역으로 변경한다.")
    @Test
    void updateRemoveLineBetweenSection() {
        // given
        Section removeSection = new Section(line, 증미역, 등촌역, fourDistance);
        Section targetSection = new Section(line, 등촌역, 신목동역, fourDistance);

        // when
        targetSection.updateRemoveLineBetweenSection(removeSection);

        // then
        assertThat(targetSection.getUpStation()).isEqualTo(증미역);
        assertThat(targetSection.getDistance()).isEqualTo(8);
    }
}
