package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SectionsTest {

    Station 상행역;
    Station 하행역;
    Sections 구간_모음;

    /**
     * Given 구간에 역을 등록한다.
     */
    @BeforeEach
    void setUp(){
        상행역 = new Station(1L, 기존지하철);
        하행역 = new Station(2L, 새로운지하철);
        구간_모음 = new Sections();
        구간_모음.addSection(Section.of(상행역,하행역,역간_거리));
    }

    /**
     *  Given 구간에 역을 등록한다.
     *  When  구간에 특정 역이 등록되어 있는지 검색한다.
     *  Then  구간에 등록된 역의 개수를 검사한다.
     */
    @DisplayName("특정역이 구간에 등록되어 있는 검사한다")
    @Test
    void 구간에_역이_있는지_조회한다(){

        //when
        int 상행역_개수 = 구간_모음.countStation(상행역.getId());
        int 하행역_개수 = 구간_모음.countStation(하행역.getId());
        //then
        assertThat(상행역_개수).isEqualTo(1);
        assertThat(하행역_개수).isEqualTo(1);
    }

    /**
     * When  구간 목록 중에 특정 역들이 모두 등록되어 있는지 확인 요청한다.
     * Then  구간에 등록되었는지 여부를 확인하다.
     */
    @DisplayName("신규 구간의 역들이 이미 모두 구간 모음에 등록되었는지 확인한다.")
    @Test
    void 구간_모음에_신규_구간들이_이미_등록되어_있는지_확인한다(){

        Station 처음_보는_역1 = new Station(Long.MAX_VALUE,"처음_보는_역1");
        Station 처음_보는_역2 = new Station(Long.MIN_VALUE,"처음_보는_역2");

        //when
        boolean 이미_등록된_역들 = 구간_모음.containsStation(Section.of(상행역, 하행역, 역간_거리));
        boolean 등록_되지_않은_역들 = 구간_모음.containsStation(Section.of(처음_보는_역1, 처음_보는_역2, 역간_거리));
        boolean 하나만_등록_되어있다 = 구간_모음.containsStation(Section.of(상행역, 처음_보는_역2, 역간_거리));

        //then
        assertTrue(이미_등록된_역들);
        assertFalse(등록_되지_않은_역들);
        assertFalse(하나만_등록_되어있다);
    }

    /**
     * When  구간 목록 중에 특정 역들이 하나라도 등록되어 있는지 확인 요청한다.
     * Then  구간에 등록되었는지 여부를 확인하다.
     */
    @DisplayName("구간중에 신규구간의 역들이 하나라도 등록되어 있는지 확인한다.")
    @Test
    void 구간_모음에_신규_구간들의_역이_하나라도_등록되어_있는지_확인한다(){

        Station 처음_보는_역1 = new Station(Long.MAX_VALUE,"처음_보는_역1");
        Station 처음_보는_역2 = new Station(Long.MIN_VALUE,"처음_보는_역2");

        //when
        boolean 이미_등록된_역들 = 구간_모음.stationIsNotInSection(Section.of(상행역, 하행역, 역간_거리));
        boolean 등록_되지_않은_역들 = 구간_모음.stationIsNotInSection(Section.of(처음_보는_역1, 처음_보는_역2, 역간_거리));
        boolean 하나만_등록_되어있다 = 구간_모음.stationIsNotInSection(Section.of(상행역, 처음_보는_역2, 역간_거리));

        //then
        assertFalse(이미_등록된_역들);
        assertTrue(등록_되지_않은_역들);
        assertFalse(하나만_등록_되어있다);
    }

    /**
     * Given 구간을 2개 등록한다
     * When  구간 중 상행 종점역을 찾는다.
     * Then  상행 종점역인지 확인한다.
     */
    @DisplayName("구간중에 상행 종점역을 찾는다")
    @Test
    void 구간에서_상행_종점역을_찾는다(){
        //given
        Station 처음_보는_역1 = new Station(Long.MAX_VALUE,"처음_보는_역1");
        구간_모음.addSection(Section.of(하행역,처음_보는_역1,역간_거리-1));

        //when
        Section 상행_종점_구간 = 구간_모음.findFirstSection();

        //then
        assertThat(상행_종점_구간.getUpStation().getId()).isEqualTo(상행역.getId());
        assertThat(상행_종점_구간.getDownStation().getId()).isEqualTo(하행역.getId());
    }

    /**
     * Given 구간을 2개 등록한다
     * When  구간 중 특정역을 상행역으로 가지고 있는 구간을 검색한다.
     * Then  특정역을 상행으로 가지고 있는 역을 찾았다
     */
    @DisplayName("구간중에 상행 종점역을 찾는다")
    @Test
    void 구간_모음에서_특정_역을_상행역으로_가지고_있는_구간을_찾는다(){
        //given
        Station 처음_보는_역1 = new Station(Long.MAX_VALUE,"처음_보는_역1");
        구간_모음.addSection(Section.of(하행역, 처음_보는_역1, 역간_거리 - 1));

        //when
        Section 상행_종점_구간 = 구간_모음.findSectionByUpStation(하행역.getId());

        //then
        assertThat(상행_종점_구간.getUpStation().getId()).isEqualTo(하행역.getId());
        assertThat(상행_종점_구간.getDownStation().getId()).isEqualTo(처음_보는_역1.getId());
    }

    /**
     * When  구간 앞에 하나의 구간을 추가한다.
     * Then  구간이 추가된다
     */
    @DisplayName("구간 앞에 구간을 추가한다.")
    @Test
    void 구간_앞에_구간을_추가한다() {
        //given
        Station 처음_보는_역1 = new Station(Long.MAX_VALUE, "처음_보는_역1");
        구간_모음.addSection(Section.of(처음_보는_역1, 상행역, 역간_거리 - 1));

        //when
        Section 상행_종점_구간 = 구간_모음.findFirstSection();

        //then
        assertThat(상행_종점_구간.getUpStation().getId()).isEqualTo(처음_보는_역1.getId());
        assertThat(상행_종점_구간.getDownStation().getId()).isEqualTo(상행역.getId());
    }

    /**
     * When  구간 중간에 구간을 추가한다.
     * Then  구간이 추가된다
     */
    @DisplayName("구간 중간에 구간을 추가한다.")
    @Test
    void 구간_중간에_구간을_추가한다() {
        //given
        Station 처음_보는_역1 = new Station(Long.MAX_VALUE, "처음_보는_역1");
        구간_모음.addSection(Section.of(상행역, 처음_보는_역1, 역간_거리 - 1));

        //when
        Section 상행_종점_구간 = 구간_모음.findFirstSection();

        //then
        assertThat(상행_종점_구간.getUpStation().getId()).isEqualTo(상행역.getId());
        assertThat(상행_종점_구간.getDownStation().getId()).isEqualTo(처음_보는_역1.getId());
    }

    /**
     * When  구간 끝에 구간을 추가한다.
     * Then  구간이 추가된다
     */
    @DisplayName("구간 끝에 구간을 추가한다.")
    @Test
    void 구간_끝에_구간을_추가한다() {
        //given
        Station 처음_보는_역1 = new Station(Long.MAX_VALUE, "처음_보는_역1");
        구간_모음.addSection(Section.of(하행역, 처음_보는_역1, 역간_거리 - 1));

        //when
        Section 하행_종점_구간 = 구간_모음.findSectionByUpStation(하행역.getId());

        //then
        assertThat(하행_종점_구간.getUpStation().getId()).isEqualTo(하행역.getId());
        assertThat(하행_종점_구간.getDownStation().getId()).isEqualTo(처음_보는_역1.getId());
    }

    /**
     * Given 기존 구간보다 긴 구간을 생성한다.
     * When  신규 구간을 중간에 등록 요청한다.
     * Then  구간 등록이 실패한다.
     */
    @DisplayName("기존 구간보다 긴 구간은 중간에 넣을 수 없다")
    @Test
    void 긴_구간은_중간에_넣을_수_없다() {
        //given
        Station 처음_보는_역1 = new Station(Long.MAX_VALUE, "처음_보는_역1");


        //when //then
        assertThrows(BusinessException.class, () -> {
            구간_모음.addSection(Section.of(상행역, 처음_보는_역1, Integer.MAX_VALUE));
            구간_모음.addSection(Section.of(처음_보는_역1, 하행역, Integer.MAX_VALUE));
        });
    }
}
