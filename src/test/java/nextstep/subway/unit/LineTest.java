package nextstep.subway.unit;

import nextstep.subway.applicaion.exceptions.InvalidStationParameterException;
import nextstep.subway.applicaion.exceptions.SectionNotEnoughException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {
    private Station 당곡역;
    private Station 보라매역;
    private Station 신림역;
    private Line 신림선;
    @BeforeEach
    void setUp() {
        당곡역 = new Station("당곡역");
        보라매역 = new Station("보라매역");
        신림역 = new Station("신림역");

        신림선 = new Line("신림선", "blue-bg-300");
    }
    @DisplayName("구간 추가하는 메서드")
    @Test
    void addSection() {
        //when
        신림선.addSection(당곡역, 보라매역, 10);

        //then
        assertThat(신림선.getSections())
                .filteredOn(section -> section.equals(new Section(신림선, 당곡역, 보라매역, 10)));
    }

    @DisplayName("구간 목록을 조회하는 메서드")
    @Test
    void getStations() {
        신림선.addSection(당곡역, 보라매역, 10);
        신림선.addSection(보라매역, 신림역, 4);

        assertAll(() -> {
            assertThat(신림선.getStation()).contains(당곡역, 보라매역, 신림역);
            assertThat(신림선.getStation().size()).isEqualTo(3);
        });
    }

    @DisplayName("구간 제거 메서드")
    @Test
    void removeSection() {
        신림선.addSection(당곡역, 보라매역, 10);
        신림선.addSection(보라매역, 신림역, 4);

        신림선.removeSection(신림역);

        assertAll(() -> {
            assertThat(신림선.getSections()).hasSize(1);
            assertThat(신림선.getSections()).doesNotContain(new Section(신림선, 보라매역, 신림역, 4));
        });
    }

    @DisplayName("구간 제거 실패 - 등록된 구간이 2개 미만일 때")
    @Test
    void removeSectionFailBoundOfArray() {
        //given
        Station 서울대역 = new Station("서울대역");

        //when
        //then
        assertThatThrownBy(() -> 신림선.removeSection(서울대역))
                .isInstanceOf(SectionNotEnoughException.class);
    }

    @DisplayName("구간 제거 실패 - 삭제하고자 하는 역이 마지막 역이 아닐 때")
    @Test
    void removeSectionsFailIsNotSameLastStation() {
        //given
        Station 서울대역 = new Station("서울대역");
        신림선.addSection(당곡역, 보라매역, 10);
        신림선.addSection(보라매역, 신림역, 4);

        //when
        //then
        assertThatThrownBy(() -> 신림선.removeSection(서울대역))
                .isInstanceOf(InvalidStationParameterException.class);
    }
}
