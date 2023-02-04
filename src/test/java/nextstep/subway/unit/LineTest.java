package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LineTest {
    @Test
    @DisplayName("구간 추가")
    void addSection() {
        //given 노선을 생성한다.
        Line line = new Line("4호선", "sky-blue");
        Station 당고개 = new Station("당고개");
        Station 상계 = new Station("상계");

        //when 노선에 구간을 추가한다.
        line.addSections(new Section(line, 당고개, 상계, 10));

        //then 노선에서 구간이 조회된다.
        Assertions.assertAll(
                () ->   assertThat(line.getSections().isEmpty()).isFalse(),
                () -> assertThat(line.getSections().equalsLastStation(상계)).isTrue(),
                () -> assertThat(line.getAllStations()
                    .stream().map(Station::getName)).containsOnly("당고개", "상계")
        );


    }

    @Test
    @DisplayName("구간 추가 실패 - 마지막역에서 출발하는 구간이 아닐 때")
    void addSectionLastStation() {
        //given 노선을 생성하고 구간을 추가한다.
        Line line = new Line("4호선", "sky-blue");
        Station 당고개 = new Station("당고개");
        Station 상계 = new Station("상계");
        line.addSections(new Section(line, 당고개, 상계, 10));

        //when 노선의 마지막역에서 출발하지 않는 구간을 추가한다.
        Station 서울역 = new Station("서울역");
        Station 숙대입구 = new Station("숙대입구");

        //then IllegalArgumentException
        assertThatIllegalArgumentException().isThrownBy(
                () -> line.addSections(new Section(line, 서울역, 숙대입구, 10)));
    }

    @Test
    @DisplayName("구간 추가 실패 - 추가하는 구간의 하행역이 이미 포함됬을때")
    void addSectionContainDownStation() {
        //given 노선을 생성하고 구간을 추가한다.
        Line line = new Line("4호선", "sky-blue");
        Station 당고개 = new Station("당고개");
        Station 상계 = new Station("상계");
        line.addSections(new Section(line, 당고개, 상계, 10));

        //when 추가하는 구간의 하행역을 추가한다.
        Station 서울역 = new Station("서울역");

        //then IllegalArgumentException
        assertThatIllegalArgumentException().isThrownBy(
                () -> line.addSections(new Section(line, 서울역, 상계, 10)));
    }

    @Test
    @DisplayName("노선의 모든 역 조회")
    void getStations() {
        //given 노선과 역을 생성한다.
        Line line = new Line("4호선", "sky-blue");
        Station 당고개 = new Station("당고개");
        Station 상계 = new Station("상계");
        Station 노원 = new Station("노원");

        //when 노선에 구간을 추가한다.
        line.addSections(new Section(line, 당고개, 상계, 10));
        line.addSections(new Section(line, 상계, 노원, 5));

        //then 노선의 모든 역을 조회한다.
        Assertions.assertAll(
                () -> assertThat(line.getSections().isEmpty()).isFalse(),
                () -> assertThat(line.getSections().equalsLastStation(노원)).isTrue(),
                () -> assertThat(line.getAllStations()
                        .stream().map(Station::getName)).containsOnly("당고개", "상계", "노원"),
                () -> assertThat(line.getAllStations()).hasSize(3)
        );

    }

    @Test
    @DisplayName("구간 삭제")
    void removeSection() {
        //given 노선과 역을 생성하고 구간 2개를 추가한다.
        Line line = new Line("4호선", "sky-blue");
        Station 당고개 = new Station("당고개");
        Station 상계 = new Station("상계");
        Station 노원 = new Station("노원");
        line.addSections(new Section(line, 당고개, 상계, 10));
        line.addSections(new Section(line, 상계, 노원, 5));

        //when 구간을 삭제한다.
        line.removeLastSection();

        //then 삭제된 역이 노선에서 조회되지 않는다.
        Assertions.assertAll(
                () -> assertThat(line.getSections().isEmpty()).isFalse(),
                () -> assertThat(line.getSections().equalsLastStation(상계)).isTrue(),
                () -> assertThat(line.getAllStations()
                        .stream().map(Station::getName)).containsOnly("당고개", "상계"),
                () -> assertThat(line.getAllStations()).hasSize(2)
        );
    }

}
