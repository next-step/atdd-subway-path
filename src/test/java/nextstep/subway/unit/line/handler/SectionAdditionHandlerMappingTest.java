package nextstep.subway.unit.line.handler;

import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.handler.*;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class SectionAdditionHandlerMappingTest {

    final int SECTION_DEFAULT_DISTANCE = 10;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 익명역;
    Line 이호선;

    SectionAdditionHandlerMapping handlerMapping = new SectionAdditionHandlerMapping();


    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        이호선 = new Line("이호선", "Green", SECTION_DEFAULT_DISTANCE, 강남역, 역삼역);
    }

    @Test
    void getAddSectionAtLastHandler() {
        // given
        Section newSection = new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);

        // then
        assertThat(handler).isInstanceOf(AddSectionAtLastHandler.class);
        assertThat(handler.checkApplicable(이호선.getSections(), newSection)).isTrue();
    }

    @DisplayName("노선 맨 끝에 구간 추가 핸들러로 구간 추가 성공")
    @Test
    void applyAddSectionAtLastHandler() {
        // given
        Section newSection = new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);
        handler.apply(이호선.getSections(), newSection);

        // then
        assertThat(이호선.hasStation(선릉역)).isTrue();
    }

    @DisplayName("노선 맨 앞에 구간 추가 핸들러 반환")
    @Test
    void getAddSectionAtFirstHandler() {
        // given
        Section newSection = new Section(이호선, 익명역, 강남역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);

        // then
        assertThat(handler).isInstanceOf(AddSectionAtFirstHandler.class);
        assertThat(handler.checkApplicable(이호선.getSections(), newSection)).isTrue();
    }

    @DisplayName("노선 맨 앞에 구간 추가 핸들러로 구간 추가 성공")
    @Test
    void applyAddSectionAtFirstHandler() {
        // given
        Section newSection = new Section(이호선, 익명역, 강남역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);
        handler.apply(이호선.getSections(), newSection);

        // then
        assertThat(이호선.hasStation(익명역)).isTrue();
    }

    @DisplayName("존재하는 큰 구간을 상행에 가까운 작은 구간으로 만들며 구간을 추가하는 핸들러 반환")
    @Test
    void getAddSectionAtMiddleRightHandler() {
        // given
        Section newSection = new Section(이호선, 익명역, 역삼역, SECTION_DEFAULT_DISTANCE - 1);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);

        // then
        assertThat(handler).isInstanceOf(AddSectionAtMiddleRightHandler.class);
        assertThat(handler.checkApplicable(이호선.getSections(), newSection)).isTrue();
    }

    @DisplayName("존재하는 큰 구간을 상행에 가까운 작은 구간으로 만들며 구간을 추가하는 핸들러로 구간 추가 성공")
    @Test
    void applyAddSectionAtMiddleRightHandler() {
        // given
        Section newSection = new Section(이호선, 익명역, 역삼역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);
        handler.apply(이호선.getSections(), newSection);

        // then
        assertThat(이호선.hasStation(익명역)).isTrue();
    }

    @DisplayName("존재하는 큰 구간을 하행에 가까운 작은 구간으로 만들며 구간을 추가하는 핸들러 반환")
    @Test
    void getAddSectionAtMiddleLeftHandler() {
        // given
        Section newSection = new Section(이호선, 강남역, 익명역, SECTION_DEFAULT_DISTANCE - 1);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);

        // then
        assertThat(handler).isInstanceOf(AddSectionAtMiddleLeftHandler.class);
        assertThat(handler.checkApplicable(이호선.getSections(), newSection)).isTrue();
    }

    @DisplayName("존재하는 큰 구간을 하행에 가까운 작은 구간으로 만들며 구간을 추가하는 핸들러로 구간 추가 성공")
    @Test
    void applyAddSectionAtMiddleLeftHandler() {
        // given
        Section newSection = new Section(이호선, 강남역, 익명역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);
        handler.apply(이호선.getSections(), newSection);

        // then
        assertThat(이호선.hasStation(익명역)).isTrue();
    }
}
