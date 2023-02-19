package nextstep.step2.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("구간 관리 - 추가 도메인 테스트")
public class SectionTest {


    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Section 강남_역삼_구간;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 10);

        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(선릉역, "id", 3L);
        ReflectionTestUtils.setField(강남_역삼_구간, "id", 1L);
    }

    /**
     * When 구간이 없는 노선에서 역 삭제를 시도하면
     * Then 예외를 발생시킨다.
     */
    @Test
    @DisplayName("구간 제거: 삭제할 역이 노선에 존재하지 않는 경우")
    void deleteSection_fail_noSectionInLine() {
        // given
        이호선 = new Line("이호선", "#29832");
        Sections 이호선_구간_일급_컬렉션 = new Sections();
        List<Section> 이호선_구간_리스트 = new ArrayList<>();

        ReflectionTestUtils.setField(이호선_구간_일급_컬렉션, "sections", 이호선_구간_리스트);
        ReflectionTestUtils.setField(이호선, "id", 1L);
        ReflectionTestUtils.setField(이호선, "sections", 이호선_구간_일급_컬렉션);

        try {
            // when
            이호선.deleteSection(강남역);

        } catch(Exception e) {
            // then
            assertEquals("노선에 구간이 존재하지 않습니다.",e.getMessage());
        }
    }

    /**
     * When 노선에 존재하지 않는 역을 삭제하면
     * Then 예외를 발생시킨다.
     */
    @Test
    @DisplayName("구간 제거: 삭제할 역이 노선에 존재하지 않는 경우")
    void deleteSection_fail_stationNotExist() {
        // given
        이호선 = new Line("이호선", "#29832");

        Sections 이호선_구간_일급_컬렉션 = new Sections();
        List<Section> 이호선_구간_리스트 = new ArrayList<>();
        이호선_구간_리스트.add(강남_역삼_구간);

        ReflectionTestUtils.setField(이호선_구간_일급_컬렉션, "sections", 이호선_구간_리스트);
        ReflectionTestUtils.setField(이호선, "id", 1L);
        ReflectionTestUtils.setField(이호선, "sections", 이호선_구간_일급_컬렉션);

        try {
            // when
            이호선.deleteSection(선릉역);

        } catch(Exception e) {
            // then
            assertEquals("노선에 존재하지 않는 역입니다.",e.getMessage());
        }
    }

    /**
     * When 1개 남은 구간을 삭제할 경우
     * Then 삭제에 성공한다.
     */
    @Test
    @DisplayName("구간 제거: 구간이 1개일 경우")
    void deleteSection_success_oneSectionExist() {
        // given
        이호선 = new Line("이호선", "#29832");

        Sections 이호선_구간_일급_컬렉션 = new Sections();
        List<Section> 이호선_구간_리스트 = new ArrayList<>();
        이호선_구간_리스트.add(강남_역삼_구간);

        ReflectionTestUtils.setField(이호선_구간_일급_컬렉션, "sections", 이호선_구간_리스트);
        ReflectionTestUtils.setField(이호선, "id", 1L);
        ReflectionTestUtils.setField(이호선, "sections", 이호선_구간_일급_컬렉션);

        // when
        이호선.deleteSection(강남역);

        // then
        assertEquals(0, 이호선.getSections().size());
    }
}
