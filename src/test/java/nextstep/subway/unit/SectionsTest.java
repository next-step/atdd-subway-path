package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Line line;
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    @BeforeEach
    void setUp() {
        line = new Line("1호선", "blue");
        stationA = new Station("A");
        stationB = new Station("B");
        stationC = new Station("C");
        stationD = new Station("D");
        stationE = new Station("E");
        Section AtoB = new Section.SectionBuilder(line)
                .setUpStation(stationA)
                .setDownStation(stationB)
                .setDistance(10)
                .build();
        line.addSection(AtoB);
    }

    @Test
    @DisplayName("상행역 종점 구간 추가")
    void addSectionFirstSection() {
        //when 노선의 상행 종점을 하행역으로 하는 구간을 추가한다.
        Section CtoA = new Section.SectionBuilder(line)
                .setUpStation(stationC)
                .setDownStation(stationA)
                .setDistance(10)
                .build();
        line.addSection(CtoA);
        //then 역을 조회하면 추가한 구간의 상행역이 노선의 상행 종점이다.
        assertThat(line.getAllStations()).containsExactly(stationC, stationA, stationB);
    }

    @Test
    @DisplayName("하행역 종점 구간 추가")
    void addSectionLastSection() {
        //when 노선의 상행 종점을 하행역으로 하는 구간을 추가한다.
        Section BtoC = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        line.addSection(BtoC);
        //then 역을 조회하면 추가한 구간의 상행역이 노선의 상행 종점이다.
        assertThat(line.getAllStations()).containsExactly(stationA, stationB, stationC);
    }

    @Test
    @DisplayName("기존 노선에 상행역, 하행역 모두 없는 구간 추가")
    void addSectionNoneContain() {
        //when 노선의 상행 종점을 하행역으로 하는 구간을 추가한다.
        Section DtoE = new Section.SectionBuilder(line)
                .setUpStation(stationD)
                .setDownStation(stationE)
                .setDistance(10)
                .build();
        //then 오류 발생
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(DtoE));
    }


    @Test
    @DisplayName("기존 노선에 상행역, 하행역 모두 있는 구간 추가")
    void addSectionContainAll() {
        //given 노선에 구간을 추가한다.
        Section BtoC = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        line.addSection(BtoC);

        //when 기존 노선에 상행역, 하행역 모두 있는 구간 추가한다.
        Section AtoC = new Section.SectionBuilder(line)
                .setUpStation(stationA)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        //then 오류 발생
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(AtoC));
    }

    @Test
    @DisplayName("하행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualDownStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 하행역이 같은 구간을 추가한다.
        Section DtoC = new Section.SectionBuilder(line)
                .setUpStation(stationD)
                .setDownStation(stationC)
                .setDistance(5)
                .build();
        line.addSection(DtoC);
        //then 추가된 구간이 사이에 조회된다. 이전 노선의 길이가 줄어든다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationA, stationB, stationD, stationC),
                () -> assertThat(BtoC.getDistance()).isEqualTo(5),
                () -> assertThat(BtoC.getDownStation()).isEqualTo(stationD)
        );
    }

    @Test
    @DisplayName("길이가 더 길고 하행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualDownStationAndGreaterDistance() {
        //given 노선에 구간을 추가한다.
        Section BtoC = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 길이가 같거나 길고 하행역이 같은 구간을 추가한다.
        Section DtoC = new Section.SectionBuilder(line)
                .setUpStation(stationD)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        //then 오류 발생
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(DtoC));
    }

    @Test
    @DisplayName("상행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualUpStation() {
        //given 노선에 구간을 추가한다.
        Section BtoC = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 상행역이 같은 구간을 추가한다.
        Section BtoD = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationD)
                .setDistance(5)
                .build();
        line.addSection(BtoD);
        //then 추가된 구간이 사이에 조회된다. 이전 노선의 길이가 줄어든다.
        assertAll(
                () -> assertThat(line.getAllStations()).containsExactly(stationA, stationB, stationD, stationC),
                () -> assertThat(BtoC.getDistance()).isEqualTo(5),
                () -> assertThat(BtoC.getUpStation()).isEqualTo(stationD)
        );
    }

    @Test
    @DisplayName("길이가 더 길고 상행역이 일치하는 사이 구간 추가")
    void addSectionIntervalEqualUpStationAndGreaterDistance() {
        //given 노선에 구간을 추가한다.
        Section BtoC = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationC)
                .setDistance(10)
                .build();
        line.addSection(BtoC);
        //when 추가된 노선과 상행역이 같은 구간을 추가한다.
        Section BtoD = new Section.SectionBuilder(line)
                .setUpStation(stationB)
                .setDownStation(stationD)
                .setDistance(15)
                .build();
        //then 오류 발생
        assertThatIllegalArgumentException()
                .isThrownBy(() -> line.addSection(BtoD));
    }

}
