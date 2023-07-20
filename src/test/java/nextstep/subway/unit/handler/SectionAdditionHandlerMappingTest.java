package nextstep.subway.unit.handler;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.handler.SectionAdditionHandler;
import nextstep.subway.line.entity.handler.SectionAdditionHandlerMapping;
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
    Station 판교역;
    Line 이호선;

    SectionAdditionHandlerMapping handlerMapping = new SectionAdditionHandlerMapping();


    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        판교역 = new Station(5L, "판교역");
        이호선 = new Line("이호선", "Green", SECTION_DEFAULT_DISTANCE, 강남역, 역삼역);
    }

    // SectionAdditionalHandler를 list로 가지는지 검증
    /**
     *
     * */

    // sections와 section이 전달되면 section을 등록할 수 있는 핸들러 찾아서 반환
    /**
     * Given 구간 등록 핸들러를 가지고 있는 구간등록핸들러매핑이 존재할 때
     * When sections(역A-C)와 section(B-C)을 핸들러 매핑에 전달하면 노선 맨 끝에 구간을 추가하는 핸들러를 반환한다.
     * Then 핸들러를 가지고 section 등록을 진행할 수 있다.
     */
    @Test
    void getAddSectionAtLastHandler() {
        // given
        Section newSection = new Section(이호선, 역삼역, 선릉역, SECTION_DEFAULT_DISTANCE);

        // when
        SectionAdditionHandler handler = handlerMapping.getHandler(이호선.getSections(), newSection);

        // then
        assertThat(handler.checkApplicable(이호선.getSections(), newSection)).isTrue();
    }
}
