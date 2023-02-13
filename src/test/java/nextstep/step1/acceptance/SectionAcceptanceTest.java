package nextstep.step1.acceptance;

import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@DisplayName("구간 관리 - 추가 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {


    @Test
    @DisplayName("역 사이에 새로운 역 추가: 새로운 상행 종점역 추가")
    void addSection_success_atUpStation(){
        // give

        // when - 노선의 상행종착역을 하행역으로 하는 구간을 추가

        // then - 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있다.
    }

    @Test
    @DisplayName("역 사이에 새로운 역 추가: 새로운 하행 종점역 추가")
    void addSection_success_atDownStation(){
        // given

        // when - 노선의 하행종착역을 상행역으로 하는 구간을 추가

        // then - 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있다.
    }

    @Test
    @DisplayName("역 사이에 새로운 역 추가: 구간 중간에 구간 추가")
    void addSection_success_atMiddle(){
        // given

        // when - 기존 구간과 같은 상행역을 가진 구간을 추가

        // then - 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있고, 기존 구간이 변경된 것을 확인할 수 있다.
    }

    @Test
    @DisplayName("구간 목록 조회: 구간 정보가 상행 - 하행 순으로 정렬되어 있어야 한다.")
    void findSections_withSortedSection(){
        // given

        // when - 노선의 구간 정보를 조회하면

        // then - 구간 정보가 상행 <-> 하행 순으로 정렬되어 반환한다
    }
}

