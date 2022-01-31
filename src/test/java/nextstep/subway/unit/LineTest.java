package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

class LineTest {
    Line _2호선;
    Station 강남역;
    Station 정자역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
        _2호선 = new Line("2호선", "green", new Section(_2호선, 강남역, 정자역, 10));
    }


    @DisplayName("구간 목록 중간에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        //given
        Station 판교역 = new Station("판교역");
        Section newSection = new Section(_2호선, 강남역, 판교역, 5);

        //when
        _2호선.addSection(newSection);

        //then
        List<Station> stations = _2호선.getStations();
        Integer totalDistance = _2호선.getSections().stream().map(Section::getDistance).reduce(0, Integer::sum);

        assertThat(_2호선.getSections(),hasSize(2));
        assertThat(stations).containsExactly(강남역, 판교역, 정자역);
        assertThat(totalDistance).isEqualTo(10);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addSectionWithUpStation() {
        //given
        Station 판교역 = new Station("판교역");
        Section newSection = new Section(_2호선, 판교역, 강남역, 5);

        //when
        _2호선.addSection(newSection);

        //then
        List<Station> stations = _2호선.getStations();
        assertThat(_2호선.getSections(),hasSize(2));
        assertThat(stations).containsExactly(판교역, 강남역, 정자역);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionWithDownStation() {
        //given
        Station 판교역 = new Station("판교역");
        Section newSection = new Section(_2호선, 정자역, 판교역, 5);

        //when
        _2호선.addSection(newSection);

        //then
        List<Station> stations = _2호선.getStations();
        assertThat(_2호선.getSections(),hasSize(2));
        assertThat(stations).containsExactly(강남역, 정자역, 판교역);
    }


    @DisplayName("(예외) 구간 목록 중간에 새로운 구간을 추가할 경우 기존 노선의 길이보다 추가하는 노선의 길이가 크거나 같을 때")
    @Test
    void addSection_exception_case1() {
        //given
        Station 판교역 = new Station("판교역");
        Section newSection = new Section(_2호선, 강남역, 판교역, 10);

        //when then
        assertThrows(IllegalArgumentException.class,()->_2호선.addSection(newSection));
    }

    @DisplayName("(예외) 구간 목록을 추가 할 때, 이미 추가할 구간의 역이 노선 내에 존재할 ")
    @Test
    void addSection_exception_case2() {
        //given
        Section newSection = new Section(_2호선, 강남역, 정자역, 5);

        //when then
        assertThrows(IllegalArgumentException.class,()->_2호선.addSection(newSection));
    }


    @DisplayName("(예외) 구간 목록을 추가 할 때, 추가할 구간의 상하행 역중 노선내 전혀 포함되지 않을 때 ")
    @Test
    void addSection_exception_case3() {
        //given
        Station 판교역 = new Station("판교역");
        Station 삼성역 = new Station("삼성역");
        Section newSection = new Section(_2호선, 판교역, 삼성역, 5);

        //when then
        assertThrows(IllegalArgumentException.class,()->_2호선.addSection(newSection));
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        //when
        List<Station> stations = _2호선.getStations();
        //then
        assertThat(stations).containsExactly(강남역, 정자역);
    }

    @DisplayName("구간이 목록에서 하행 종점역 삭제")
    @Test
    void removeSectionWithDownStation() {
        //given
        Station 판교역 = new Station("판교역");
        _2호선.addSection(new Section(_2호선, 정자역, 판교역, 5));

        //when
        _2호선.removeSection(판교역);

        //then
        List<Section> sections = _2호선.getSections();
        List<Station> stations = _2호선.getStations();

        assertThat(sections, hasSize(1));
        assertThat(stations).containsExactly(강남역, 정자역);
    }

    @DisplayName("구간이 목록에서 상행 종점역 삭제")
    @Test
    void removeSectionWithUpStation() {
        //given
        Station 판교역 = new Station("판교역");
        _2호선.addSection(new Section(_2호선, 정자역, 판교역, 5));

        //when
        _2호선.removeSection(강남역);

        //then
        List<Section> sections = _2호선.getSections();
        List<Station> stations = _2호선.getStations();

        assertThat(sections, hasSize(1));
        assertThat(stations).containsExactly(정자역, 판교역);
    }

    @DisplayName("구간이 목록에서 중간 역 삭제")
    @Test
    void removeSection() {
        //given
        Station 판교역 = new Station("판교역");
        _2호선.addSection(new Section(_2호선, 정자역, 판교역, 5));

        //when
        _2호선.removeSection(정자역);

        //then
        List<Section> sections = _2호선.getSections();
        List<Station> stations = _2호선.getStations();

        assertThat(sections, hasSize(1));
        assertThat(stations).containsExactly(강남역, 판교역);
        Section 강남_판교_구간 = _2호선.getSectionByUpStation(강남역);
        assertThat(강남_판교_구간.getDistance()).isEqualTo(15);
    }

    @DisplayName("(예외) 구간이 하나 인 목록에서 역 삭제")
    @Test
    void removeSection_exception_case1() {
        //when then
        assertThrows(IllegalArgumentException.class, () -> _2호선.removeSection(정자역));
    }

    @DisplayName("(예외) 노선에 등록되지 않은 역 삭제")
    @Test
    void removeSection_exception_case2() {
      //given
      Station 판교역 = new Station("판교역");
      Station 삼성역 = new Station("삼성역");
      _2호선.addSection(new Section(_2호선, 정자역, 판교역, 5));

      //when then
      assertThrows(IllegalArgumentException.class, () -> _2호선.removeSection(삼성역));
    }

    @DisplayName("(예외) 노선에 등록되지 않은 역 삭제")
    @Test
    void removeSection_exception_case3() {
      //given
      Station 판교역 = new Station("판교역");
      Station 삼성역 = new Station("삼성역");
      _2호선.addSection(new Section(_2호선, 정자역, 판교역, 5));

      //when then
      assertThrows(IllegalArgumentException.class, () -> _2호선.removeSection(삼성역));
    }




}
