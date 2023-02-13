package nextstep.step1.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("구간 관리 - 추가 도메인 테스트")
public class SectionTest {

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Section 강남_역삼_구간;
    private Section 역삼_선릉_구간;

    @BeforeEach
    void setUp(){
        이호선 = new Line("이호선", "#29832");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 10);
        역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 5);

        ReflectionTestUtils.setField(이호선, "id", 1L);
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(선릉역, "id", 3L);
        ReflectionTestUtils.setField(삼성역, "id", 4L);
        ReflectionTestUtils.setField(강남_역삼_구간, "id", 1L);
        ReflectionTestUtils.setField(역삼_선릉_구간, "id", 2L);
    }


    @Test
    @DisplayName("역 사이에 새로운 역 추가(예외): 구간 중간에 구간 추가 시 추가할 구간의 길이는 기존 구간의 길이보다 같거나 크다면 예외 발생")
    void addSections_fail_distance(){
        // given

        // when - 구간 중간에 새로운 구간을 추가한다. (새로운 구간의 길이는 기존 구간의 길이와 같거나 더 크다)

        // then - 예외가 발생한다.
    }

    @Test
    @DisplayName("역 사이에 새로운 역 추가(예외): 상행역, 하행역 모두 노선에 등록되어 있다면 예외 발생")
    void addSections_fail_upStationAndDownStationExistInLine(){
        // given

        // when - 상행역, 하행역 모두 노선에 등록된 구간을 추가한다.

        // then - 예외가 발생한다.
    }

    @Test
    @DisplayName("역 사이에 새로운 역 추가(예외): 상행역, 하행역 모두 노선에 없다면 예외 발생")
    void addSections_fail_upStationAndDownStationNotExistInLine(){
        // given

        // when - 상행역, 하행역 모두 노선에 없는 구간을 추가한다.

        // then - 예외가 발생한다.
    }
}
