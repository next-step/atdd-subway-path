package nextstep.subway.unit.line.handler;

import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.handler.addition.SectionAdditionHandlerMapping;
import nextstep.subway.line.domain.entity.handler.deletion.*;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionDeletionHandlerTest {

    final int SECTION_DEFAULT_DISTANCE = 10;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 익명역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        이호선 = new Line("이호선", "Green", SECTION_DEFAULT_DISTANCE, 강남역, 역삼역);
    }

    /**
     * Given
     * When DeleteSectionAtTopHandler에 상행 종착역과 구간을 전달하며 구간 삭제 명령을 내리면
     * Then 구간에서 상행 종착역을 찾을 수 없다
     * */
    @DisplayName("상행 종착역이 속한 구간 제거")
    @Test
    void returnDeleteSectionAtTopHandler() {
        // given
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

        // when
        new DeleteSectionAtTopHandler().apply(이호선.getSections(), 강남역);

        // then
        assertThat(이호선.getStations()).doesNotContain(강남역);
    }

    @DisplayName("하행 종착역이 속한 구간 제거")
    @Test
    void returnDeleteSectionAtLastHandler() {
        // given
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

        // when
        new DeleteSectionAtLastHandler().apply(이호선.getSections(), 선릉역);

        // then
        assertThat(이호선.getStations()).doesNotContain(선릉역);
    }

    @DisplayName("상행 종착역과 하행종착역이 아닌 중간 구간 제거하는 핸들러 반환")
    @Test
    void returnDeleteSectionAtMiddleHandler() {
        // given
        이호선.addSection(new SectionAdditionHandlerMapping(), new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE));

        // when
        new DeleteSectionAtMiddleHandler().apply(이호선.getSections(), 선릉역);

        // then
        assertThat(이호선.getStations()).doesNotContain(선릉역);
    }
}
