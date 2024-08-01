package nextstep.subway.section.unit;

import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.section.exception.SectionException;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SectionTest {

    private Station 역삼역;
    private Station 강남역;
    private Station 선릉역;
    private Station 선정릉역;
    private Section 역삼역_강남역;
    private Section 강남역_선릉역;
    private Section 선릉역_선정릉역;

    private Sections 생성된_구간들;

    @BeforeEach
    public void setup() {
        역삼역 = Station.of(1L, "역삼역");
        강남역 = Station.of(2L, "강남역");
        선릉역 = Station.of(3L, "선릉역");
        선정릉역 = Station.of(4L, "선정릉역");
        역삼역_강남역 = Section.of(1L, 역삼역, 강남역, 2L);
        강남역_선릉역 = Section.of(2L, 강남역, 선릉역, 2L);
        선릉역_선정릉역 = Section.of(3L, 선릉역, 선정릉역, 1L);

        생성된_구간들 = new Sections();
    }

    @Test
    @DisplayName("Section의 Distance는 1 이상의 값을 가져야한다.")
    public void section_distance() {
        // when & then
        Assertions.assertThrows(SectionException.class, () -> Section.of(역삼역, 강남역, 0L))
                .getMessage().equals("구간의 길이는 최소 1 이상이어야 합니다.");
    }

    @Test
    @DisplayName("새로운 구간을 첫번째 구간에 추가한다.")
    public void add_section() {
        // when
        생성된_구간들.addSection(역삼역_강남역);

        // then
        Section 생성된_Section_조회 = 생성된_구간들.getSectionByUpStationId(역삼역.getId());
        assertEquals(역삼역_강남역, 생성된_Section_조회);
    }

    @Test
    @DisplayName("새로운 구간을 중간 구간에 추가한다.")
    public void add_section_middle() {
        // given
        생성된_구간들.addSection(역삼역_강남역);
        생성된_구간들.addSection(강남역_선릉역);

        // when
        var 중간_구간에_추가할_Section = Section.of(강남역, 선정릉역, 1L);
        생성된_구간들.addSection(중간_구간에_추가할_Section);

        // then
        var 생성된_Section_조회 = 생성된_구간들.getSectionByUpStationId(강남역.getId());
        assertEquals(중간_구간에_추가할_Section, 생성된_Section_조회);
    }

    @Test
    @DisplayName("새로운 구간을 마지막 구간에 추가한다.")
    public void add_section_last() {
        // given
        생성된_구간들.addSection(역삼역_강남역);

        // when
        var 마지막_구간에_추가할_Section = Section.of(강남역, 선릉역, 2L);
        생성된_구간들.addSection(마지막_구간에_추가할_Section);

        // then
        var 생성된_Section_조회 = 생성된_구간들.getSectionByUpStationId(강남역.getId());
        assertEquals(마지막_구간에_추가할_Section, 생성된_Section_조회);
    }

    @Test
    @DisplayName("새로운 구간의 상행역과 하행역 둘 다 기존 노선에 존재하면 구간 생성은 실패한다.")
    public void add_section_fail1() {
        // given
        생성된_구간들.addSection(역삼역_강남역);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> 생성된_구간들.addSection(역삼역_강남역))
                .getMessage().equals("새로운 구간의 상행역과 하행역이 이미 등록되어 있습니다.");

    }

    @Test
    @DisplayName("기존에 등록된 구간이 존재할 때, 새로운 구간의 상행역과 하행역 둘 다 기존 노선에 존재하지 않으면 구간 생성은 실패한다.")
    public void add_section_fail2() {
        // given
        생성된_구간들.addSection(역삼역_강남역);
        var 구간에_추가할_Section = Section.of(선정릉역, 선릉역, 2L);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> 생성된_구간들.addSection(구간에_추가할_Section))
                .getMessage().equals("구간을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("기존 구간의 Distance보다 길거나 같은 Distance를 가진 구간 생성은 실패한다.")
    public void add_section_fail3() {
        // given
        생성된_구간들.addSection(역삼역_강남역);
        var 구간에_추가할_Section = Section.of(역삼역, 선릉역, 2L);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> 생성된_구간들.addSection(구간에_추가할_Section))
                .getMessage().equals("새로운 구간의 길이는 기존의 구간 길이보다 길어야합니다.");

    }

    @Test
    @DisplayName("상행 종점역을 가진 첫 번째 구간을 삭제한다.")
    public void delete_section_first_section() {
        // given
        생성된_구간들.addSection(역삼역_강남역);
        생성된_구간들.addSection(강남역_선릉역);

        // when
        생성된_구간들.removeSection(역삼역_강남역);

        // then
        var 삭제_후_강남역_선릉역_구간 = 생성된_구간들.getSectionByUpStationId(강남역.getId());
        Assertions.assertAll(
                () -> assertEquals(생성된_구간들.getSections().size(), 1),
                () -> assertNull(삭제_후_강남역_선릉역_구간.getNextSection()),
                () -> assertEquals(삭제_후_강남역_선릉역_구간.getUpStation(), 강남역),
                () -> assertEquals(삭제_후_강남역_선릉역_구간.getDownStation(), 선릉역),
                () -> assertEquals(삭제_후_강남역_선릉역_구간.getDistance(), 2L)
        );

    }

    @Test
    @DisplayName("중간 구간을 삭제한다.")
    public void delete_section_middle_section() {
        // given
        생성된_구간들.addSection(역삼역_강남역);
        생성된_구간들.addSection(강남역_선릉역);
        생성된_구간들.addSection(선릉역_선정릉역);

        // when
        생성된_구간들.removeSection(강남역_선릉역);

        // then
        var 삭제_후_역삼역_선릉역_구간 = 생성된_구간들.getSectionByUpStationId(역삼역.getId());
        var 삭제_후_선릉역_선정릉역_구간 = 생성된_구간들.getSectionByUpStationId(선릉역.getId());

        assertEquals(생성된_구간들.getSections().size(), 2);
        assertNull(삭제_후_역삼역_선릉역_구간.getPreviousSection());
        assertEquals(삭제_후_역삼역_선릉역_구간.getNextSection(), 삭제_후_선릉역_선정릉역_구간);
        assertEquals(삭제_후_역삼역_선릉역_구간.getUpStation(), 역삼역);
        assertEquals(삭제_후_역삼역_선릉역_구간.getDownStation(), 선릉역);
        assertEquals(삭제_후_역삼역_선릉역_구간.getDistance(), 4L);

        assertEquals(삭제_후_선릉역_선정릉역_구간.getPreviousSection(), 역삼역_강남역);
        assertNull(삭제_후_선릉역_선정릉역_구간.getNextSection());
        assertEquals(삭제_후_선릉역_선정릉역_구간.getUpStation(), 선릉역);
        assertEquals(삭제_후_선릉역_선정릉역_구간.getDownStation(), 선정릉역);
        assertEquals(삭제_후_선릉역_선정릉역_구간.getDistance(), 1L);
    }

    @Test
    @DisplayName("하행 종점역을 가진 마지막 구간을 삭제한다.")
    public void delete_section_last_section() {
        // given
        생성된_구간들.addSection(역삼역_강남역);
        생성된_구간들.addSection(강남역_선릉역);
        생성된_구간들.addSection(선릉역_선정릉역);

        // when
        생성된_구간들.removeSection(선릉역_선정릉역);

        // then
        var 삭제_후_강남역_선릉역_구간 = 생성된_구간들.getSectionByUpStationId(강남역.getId());
        assertEquals(생성된_구간들.getSections().size(), 2);
        assertNull(삭제_후_강남역_선릉역_구간.getNextSection());
        assertEquals(삭제_후_강남역_선릉역_구간.getPreviousSection(), 역삼역_강남역);
        assertEquals(삭제_후_강남역_선릉역_구간.getUpStation(), 강남역);
        assertEquals(삭제_후_강남역_선릉역_구간.getDownStation(), 선릉역);
        assertEquals(삭제_후_강남역_선릉역_구간.getDistance(), 2L);

    }

    @Test
    @DisplayName("구간이 2개 미만일 때, 구간 삭제 요청은 실패한다.")
    public void delete_section_fail1() {
        // given
        생성된_구간들.addSection(역삼역_강남역);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> 생성된_구간들.removeSection(역삼역_강남역))
                .getMessage().equals("구간은 최소 1개 이상이어야 합니다.");

    }

    @Test
    @DisplayName("노선에 존재하지 않는 역을 가진 구간의 삭제 요청은 실패한다.")
    public void delete_section_fail2() {
        생성된_구간들.addSection(역삼역_강남역);
        생성된_구간들.addSection(강남역_선릉역);

        // when & then
        Assertions.assertThrows(SectionException.class, () -> 생성된_구간들.removeSection(선릉역_선정릉역))
                .getMessage().equals("구간을 찾을 수 없습니다.");

    }

}