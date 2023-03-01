package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    private Line 강남_2호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        강남_2호선 = new Line("2호선", "green");
        강남역 = new Station();
        역삼역 = new Station();
        삼성역 = new Station();
    }

    @Test
    @DisplayName("구간을 추가 테스트")
    void addSection() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);

        // when
        강남_2호선.addSection(강남_역삼_구간);

        // then
        assertThat(강남_2호선.getSections()).containsExactly(강남_역삼_구간);
    }

    @Test
    @DisplayName("노선의 역 목록 테스트")
    void getStations() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 역삼_삼성_구간 = new Section(강남_2호선, 역삼역, 삼성역, 12);
        강남_2호선.addSection(강남_역삼_구간);
        강남_2호선.addSection(역삼_삼성_구간);

        // when
        List<Station> stations = 강남_2호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    @DisplayName("노선에 구간이 비어 있는 경우 구간을 삭제 할 수 없다")
    void removeSectionFailBySectionsIsEmpty() {
        assertThatThrownBy(() -> 강남_2호선.removeSection())
                .isInstanceOf(SectionBadRequestException.class)
                .hasMessage("구간이 존재하지 않습니다");
    }

    @Test
    void 노선의_구간이_1개일_경우_구간을_삭제_할_수_없다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));

        // then
        assertThatThrownBy(() -> 강남_2호선.removeSection())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 노선의_마지막_구간을_삭제한다() {
        // given
        강남_2호선.addSection(new Section(강남_2호선, 강남역, 역삼역, 10));
        강남_2호선.addSection(new Section(강남_2호선, 역삼역, 삼성역, 12));

        // when
        강남_2호선.removeSection();

        // then
        assertThat(강남_2호선.getSections().size()).isEqualTo(1);
    }
}
