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

    Station 청계산입구역;
    Station 판교역;
    Line 신분당선;
    Section 청계산입구역_판교역_구간;
    final Integer 신분당선_구간_크기 = 1;
    @BeforeEach
    void init(){
        청계산입구역 = new Station("청계산입구역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "yellow");
        청계산입구역_판교역_구간 = new Section(신분당선, 청계산입구역, 판교역, 10);
        신분당선.getSections().add(청계산입구역_판교역_구간);
    }


    @Test
    @DisplayName("구간 추가")
    void addSection() {
        //given
        final Station 정자역 = new Station("정자역");
        Section 판교역_정자역_구간 = new Section(신분당선, 판교역, 정자역, 10);

        //when
        신분당선.addSection(판교역_정자역_구간);

        //then
        assertThat(신분당선.getSections().size()).isEqualTo(신분당선_구간_크기 + 1);
        assertThat(신분당선.getSections()).contains(판교역_정자역_구간);
    }

    @Test
    @DisplayName("구간 조회")
    void getStations() {
        //when
        List<Section> sections = 신분당선.getSections();

        //then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.get(0)).isEqualTo(청계산입구역_판교역_구간);
    }

    @Test
    @DisplayName("구간 삭제")
    void removeSection() {
        //when
        신분당선.removeSection(청계산입구역_판교역_구간);

        //then
        List<Section> sections = 신분당선.getSections();
        assertThat(sections.size()).isEqualTo(0);
    }
}
