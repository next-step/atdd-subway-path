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

import java.util.List;

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

    @DisplayName("새로운 구간을 기존 노선 사이에 추가하기")
    @Test
    void addSectionBetweenSection() {
        //given
        신림선.addSection(보라매역, 당곡역, 3);
        신림선.addSection(당곡역, 신림역, 5);

        //when
        Station 서울벤처타운역 = new Station("서울벤처타운역");
        신림선.addSection(당곡역, 서울벤처타운역, 2);

        //then
        assertAll(
                () -> assertThat(신림선.getStation()).containsExactly(보라매역, 당곡역, 서울벤처타운역, 신림역),
                () -> assertThat(신림선.getDistance()).containsExactly(3,2,3)
        );
    }

    @DisplayName("새로운 구간을 기존 노선의 상행 종점으로 추가하기")
    @Test
    void addSectionAlreadyTopSection() {
        //given
        신림선.addSection(보라매역, 당곡역, 3);
        신림선.addSection(당곡역, 신림역, 5);

        //when
        Station 서울벤처타운역 = new Station("서울벤처타운역");
        신림선.addSection(서울벤처타운역, 보라매역, 10);

        //then
        assertAll(
                () -> assertThat(신림선.getSections().size()).isEqualTo(3),
                () -> assertThat(신림선.getStation()).containsExactly(서울벤처타운역, 보라매역, 당곡역, 신림역)
        );
    }

    @DisplayName("새로운 구간을 기존 노선의 하행 종점으로 추가하기")
    @Test
    void addSectionAlreadyBottomSection() {
        //given
        신림선.addSection(보라매역, 당곡역, 3);
        신림선.addSection(당곡역, 신림역, 5);

        //when
        Station 서울벤처타운역 = new Station("서울벤처타운역");
        신림선.addSection(신림역, 서울벤처타운역, 10);

        //then
        assertAll(
                () -> assertThat(신림선.getSections().size()).isEqualTo(3),
                () -> assertThat(신림선.getStation()).containsExactly(보라매역, 당곡역, 신림역, 서울벤처타운역),
                () -> assertThat(신림선.getDistance()).containsExactly(3,5,10)
        );
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
