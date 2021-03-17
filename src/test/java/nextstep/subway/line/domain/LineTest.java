package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.domain.exception.InvalidDistanceException;
import nextstep.subway.line.domain.exception.NoneOfStationEnrolledException;
import nextstep.subway.line.domain.exception.OnlyOneSectionRemainException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {

    Station 강남역1;
    Station 선릉역2;
    Station 역삼역3;
    Station 잠실역4;
    Line line;

    @BeforeEach
    void setUp(){
        //given
        강남역1 = new Station("강남역1"); //repository를 써서 꺼내오지 않는 이유? 기능만 보기 위해서?
        ReflectionTestUtils.setField(강남역1, "id", 1L);
        선릉역2 = new Station("선릉역2");
        ReflectionTestUtils.setField(선릉역2, "id", 2L);
        역삼역3 = new Station("역삼역3");
        ReflectionTestUtils.setField(역삼역3, "id", 3L);
        잠실역4 = new Station("잠실역4");
        ReflectionTestUtils.setField(잠실역4, "id", 4L);

        line = new Line("2호선", "green", 강남역1, 선릉역2, 10);

    }

    @Test
    void getStations() {
        //then
        assertThat(line.getSections().getStations().size()).isEqualTo(2);
    }

    @Test
    void addSection() {
        //when
        line.addSection(선릉역2, 역삼역3, 10);
        line.addSection(역삼역3, 잠실역4, 10);
        //then
        List<String> resultList = line.getSections().getStations().stream().map(station -> station.getName()).collect(Collectors.toList());
        assertThat(resultList).containsExactlyElementsOf(Arrays.asList("강남역1", "선릉역2", "역삼역3", "잠실역4"));
    }

    @DisplayName("목록 중간에 추가 가능(upStation 매칭시)")
    @Test
    void addSectionInMiddleWhenUpStationMatch() {
        //when
        line.addSection(강남역1, 잠실역4, 3);
        //then
        List<String> resultList = line.getSections().getStations().stream().map(station -> station.getName()).collect(Collectors.toList());
        assertThat(resultList).containsExactlyElementsOf(Arrays.asList("강남역1", "잠실역4", "선릉역2"));
        assertThat(line.getSections().getSectionList().get(1).getDistance()).isEqualTo(7);
    }

    @DisplayName("목록 중간에 추가 가능(downStation 매칭시)")
    @Test
    void addSectionInMiddleWhenDownStationMatch() {
        //when
        line.addSection(선릉역2, 역삼역3, 10);
        line.addSection(잠실역4, 선릉역2, 4);
        //then
        List<String> resultList = line.getSections().getStations().stream().map(station -> station.getName()).collect(Collectors.toList());
        assertThat(resultList).containsExactlyElementsOf(Arrays.asList("강남역1", "잠실역4", "선릉역2", "역삼역3"));
        assertThat(line.getSections().getSectionList().get(0).getDistance()).isEqualTo(6);
    }

    @DisplayName("추가하는 섹션에 따른 수정되는 섹션의 거리가 잘못된 경우")
    @Test
    void invokeInvalidDistance() {
        //when, then
        assertThatThrownBy(() -> line.addSection(잠실역4, 선릉역2, 20))
                .isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        //when, then
        assertThatThrownBy(() -> line.addSection(강남역1, 선릉역2, 20))
                .isInstanceOf(BothStationAlreadyEnrolledException.class);
    }

    @DisplayName("두 역 모두 등록되지 않았을 때 에러 발생")
    @Test
    void noneOfStationEnrolled() {
        //given
        Station 대청역5 = new Station("대청역5");
        //when, then
        assertThatThrownBy(() -> line.addSection(잠실역4, 대청역5, 20))
                .isInstanceOf(NoneOfStationEnrolledException.class);
    }

    @DisplayName("중간역 삭제")
    @Test
    void removeSectionMiddleStation() {
        //given
        line.addSection(선릉역2, 역삼역3, 10);
        line.addSection(역삼역3, 잠실역4, 20);
        //when
        line.getSections().removeSection(역삼역3);
        //then
        assertThat(line.getSections().getStations()
                .stream().map(station -> station.getName())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(Arrays.asList("강남역1", "선릉역2", "잠실역4"));
    }

    @DisplayName("처음역 삭제")
    @Test
    void removeSectionFirstStation() {
        //given
        line.addSection(선릉역2, 역삼역3, 10);
        line.addSection(역삼역3, 잠실역4, 20);
        //when
        line.getSections().removeSection(강남역1);
        //then
        assertThat(line.getSections().getStations()
                .stream().map(station -> station.getName())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(Arrays.asList("선릉역2", "역삼역3", "잠실역4"));
    }

    @DisplayName("마지막역 삭제")
    @Test
    void removeSectionLastStation() {
        //given
        line.addSection(선릉역2, 역삼역3, 10);
        line.addSection(역삼역3, 잠실역4, 20);
        //when
        line.getSections().removeSection(잠실역4);
        //then
        assertThat(line.getSections().getStations()
                .stream().map(station -> station.getName())
                .collect(Collectors.toList()))
                .containsExactlyElementsOf(Arrays.asList("강남역1", "선릉역2", "역삼역3"));
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        //when, then
        assertThatThrownBy(() -> line.getSections().removeSection(선릉역2))
                .isInstanceOf(OnlyOneSectionRemainException.class);
    }
}
