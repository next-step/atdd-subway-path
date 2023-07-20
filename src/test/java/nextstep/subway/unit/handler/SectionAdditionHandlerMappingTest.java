package nextstep.subway.unit.handler;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.handler.*;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재할 때
     * When sections(역A-역C)와 section(역B-역C)을 핸들러 매핑에 전달하면 노선 맨 끝에 구간을 추가하는 핸들러를 반환한다.
     * Then 핸들러를 가지고 section 등록을 진행할 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재하고
     * AND 구간 강남역(A)-역삼역(C)으로 이호선이 초기화 돼있을 때
     * When 노선 맨 끝에 구간을 추가하는 핸들러에 section(역C-역B)를 sections(역A-역C) 내에 추가하라고 전달하면
     * Then sections 내에 역B를 찾을 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재할 때
     * When sections(역A-C)와 section(B-A)을 핸들러 매핑에 전달하면 노선 맨 앞에 구간을 추가하는 핸들러를 반환한다.
     * Then 핸들러를 가지고 section 등록을 진행할 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재하고
     * AND 구간 강남역(A)-역삼역(C)으로 이호선이 초기화 돼있을 때
     * When 노선 맨 끝에 구간을 추가하는 핸들러에 section(역B-역A)를 sections(역A-역C) 내에 추가하라고 전달하면
     * Then sections 내에 역B를 찾을 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재할 때
     * When sections(역A-C)와 section(B-C)을 핸들러 매핑에 전달하면 노선 중간에 구간을 추가하는 핸들러를 반환한다.
     * Then 핸들러를 가지고 section 등록을 진행할 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재하고
     * AND 구간 강남역(A)-역삼역(C)으로 이호선이 초기화 돼있을 때
     * When 노선 맨 끝에 구간을 추가하는 핸들러에 section(역B-역C)를 sections(역A-역C) 내에 추가하라고 전달하면
     * Then sections 내에 역B를 찾을 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재할 때
     * When sections(역A-C)와 section(A-B)을 핸들러 매핑에 전달하면 노선 중간에 구간을 추가하는 핸들러를 반환한다.
     * Then 핸들러를 가지고 section 등록을 진행할 수 있다.
     */
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

    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재하고
     * AND 구간 강남역(A)-역삼역(C)으로 이호선이 초기화 돼있을 때
     * When 노선 맨 끝에 구간을 추가하는 핸들러에 section(역A-역B)를 sections(역A-역C) 내에 추가하라고 전달하면
     * Then sections 내에 역B를 찾을 수 있다.
     */
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
