package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static nextstep.subway.unit.LineTestUtil.*;
class LineTest {

    Line 일호선 = new Line(일호선ID, 일호선이름, 라인색  );


    @BeforeEach
    void setUp(){
        일호선.addSection(1L, 개봉역, 구일역, DEFAULT_DISTANCE);
    }

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {
        //given

        //when

        //then
        assertThat(일호선.getSections()).filteredOn(
                section -> section.equals(new Section(일호선, 개봉역, 구일역, DEFAULT_DISTANCE))
        );

    }

    @Test
    @DisplayName("구간목록을 조회한다.")
    void getStations() {
        //given

        //when
        List<Station> stations = 일호선.getStations();

        //then
        assertAll(
                () -> assertThat(stations).filteredOn(
                        station -> station.toString().contains(개봉역.toString())
                ),
                () -> assertThat(stations).filteredOn(
                        station -> station.toString().contains(구일역.toString())
                )
        );


    }

    @Test
    @DisplayName("구간을 제거한다.")
    void removeSection() {
        Station 구로역 = new Station(3L, "구로역");
        //given
        일호선.addSection(2L, 구일역, 구로역, DEFAULT_DISTANCE);

        Section 삭제대상구간 = 일호선.getSectionById(2L);
        //when
        일호선.removeSection(구로역.getId());

        //then
        assertThat(일호선.getSections()).hasSize(1);

    }

}
