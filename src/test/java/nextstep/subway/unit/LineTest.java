package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class LineTest {

    private Line 신분당선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Section 강남_역삼_구간;
    private Section 역삼_선릉_구간;

    @BeforeEach
    void setUp(){
        신분당선 = new Line("신분당신", "#29832");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        강남_역삼_구간 = new Section(신분당선, 강남역, 역삼역, 10);
        역삼_선릉_구간 = new Section(신분당선, 역삼역, 선릉역, 5);

        ReflectionTestUtils.setField(신분당선, "id", 1L);
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(선릉역, "id", 3L);
        ReflectionTestUtils.setField(삼성역, "id", 4L);
        ReflectionTestUtils.setField(강남_역삼_구간, "id", 1L);
        ReflectionTestUtils.setField(역삼_선릉_구간, "id", 2L);
    }


    @Test
    @DisplayName("구간 추가 테스트")
    void addSection() {

        // when
        신분당선.getSections().add(강남_역삼_구간);

        // then
        assertThat(신분당선.getSections()).contains(강남_역삼_구간);
    }

    @Test
    @DisplayName("구간 조회 테스트")
    void getStations() {
        // given
        신분당선.getSections().add(강남_역삼_구간);
        신분당선.getSections().add(역삼_선릉_구간);

        // when
        List<Section> sections = 신분당선.getSections();

        // then
        assertThat(신분당선.getSections()).contains(강남_역삼_구간, 역삼_선릉_구간);
    }

    @Test
    @DisplayName("구간 삭제 테스트")
    void removeSection() {
        // given
        신분당선.getSections().add(강남_역삼_구간);
        신분당선.getSections().add(역삼_선릉_구간);

        // when
        신분당선.getSections().remove(역삼_선릉_구간);

        // then
        assertThat(신분당선.getSections()).doesNotContain(역삼_선릉_구간);
    }
}
