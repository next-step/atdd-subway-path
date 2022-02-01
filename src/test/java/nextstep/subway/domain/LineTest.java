package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.exception.BusinessException;
import nextstep.subway.applicaion.exception.DuplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    /**
     *  Given 노선을 생성한다.
     *  When  구간 중간에 새로운 구간 추가를 요청한다.
     *  Then  구간이 등록된다.
     */

    @DisplayName("노선 중간에 구간을 추가할 수 있다.")
    @Test
    void isNotDownStation() {
        //given
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        Station 중간역 = new Station(3L, "중간역");
        line.addSection(Section.of(상행역, 하행역, 역간_거리));

        //when
        line.addSection(Section.of(상행역, 중간역, 역간_거리 - 1));

        //then
        assertThat(line.getSectionSize()).isEqualTo(2);
    }

    /**
     *   Given  구간 2개를 가진 노선을 등록한다.
     *   When   이미 노선에 모두 사용중인 역을 구간 등록 요청한다.
     *   Then   구간 등록에 실패한다.
     */

    @DisplayName("이미 노선에 모두 등록된 역은 구간에 등록이 불가하다")
    @Test
    void 노선에_등록된_역들은_추가_등록이_불가하다() {
        //given
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        Station 중간역 = new Station(3L, "중간역");
        line.addSection(Section.of(상행역, 하행역, 역간_거리));
        line.addSection(Section.of(상행역, 중간역, 역간_거리 - 1));

        //when //then
        Assertions.assertThrows(DuplicationException.class, () ->
                line.addSection(Section.of(상행역, 하행역, 역간_거리 - 2))
        );

    }

    /**
     *   Given  구간 2개를 가진 노선을 등록한다.
     *   When   노선에서 하나도 사용중이지 않은 역들을 구간 등록을 요청한다.
     *   Then   구간 등록에 실패한다.
     */

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간에 추가될 수 없다")
    @Test
    void 노선에_없는_역을_둘_다_가지고_있는_노선은_추가가_불가하다() {
        //given
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        Station 중간역 = new Station(3L, "중간역");
        line.addSection(Section.of(상행역, 하행역, 역간_거리));
        line.addSection(Section.of(상행역, 중간역, 역간_거리 - 1));


        //when //then
        Station 모르는A역 = new Station(4L, "모르는A역");
        Station 모르는B역 = new Station(5L, "모르는B역");
        Assertions.assertThrows(BusinessException.class, () ->
                line.addSection(Section.of(모르는A역, 모르는B역, 역간_거리 - 2))
        );
    }

    /**
     *   Given  노선을 등록한다
     *   When   구간 삭제를 요청한다
     *   Then   구간 삭제에 실패한다.
     */
    @DisplayName("하나 남은 구간은 삭제가 불가하다")
    @Test
    void 하나_남은_구간_삭제_불가하다(){
        Line line = new Line();
        Station 상행역 = new Station(1L, 기존지하철);
        Station 하행역 = new Station(2L, 새로운지하철);
        line.addSection(Section.of(상행역, 하행역, 역간_거리));

        Assertions.assertThrows(BusinessException.class, () ->
                line.deleteSection(하행역.getId())
        );
    }
}
